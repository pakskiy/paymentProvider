package com.pakskiy.paymentProvider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.dto.payout.PayoutRequestDto;
import com.pakskiy.paymentProvider.dto.payout.PayoutResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.CountryRepository;
import com.pakskiy.paymentProvider.repository.CurrencyRepository;
import com.pakskiy.paymentProvider.repository.LanguageRepository;
import com.pakskiy.paymentProvider.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {
    private final MerchantService merchantService;
    private final PaymentRepository paymentRepository;
    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepository;
    private final LanguageRepository languageRepository;
    private final AccountService accountService;
    private final DatabaseClient client;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @SneakyThrows
    @Transactional
    public Mono<PayoutResponseDto> create(PayoutRequestDto payoutRequestDto, String token) {
        MerchantEntity merchantEntity = merchantService.checkByToken(token).toFuture().join();

        if (merchantEntity != null) {
            check(payoutRequestDto);
            Long merchantId = merchantEntity.getId();
            Optional<AccountEntity> accountEntityOptional = accountService.getByMerchantId(merchantId);
            if (accountEntityOptional.isPresent()) {
                long newDeposit = accountEntityOptional.get().getDepositAmount() + payoutRequestDto.getAmount();

                AtomicReference<Long> transactionId = new AtomicReference<>(0L);
                AccountEntity accountEntity = AccountEntity.builder()
                        .merchantId(merchantId).depositAmount(newDeposit)
                        .limitAmount(accountEntityOptional.get().getLimitAmount())
                        .isOverdraft(accountEntityOptional.get().getIsOverdraft())
                        .createdAt(accountEntityOptional.get().getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();

                String customerData = objectMapper.writeValueAsString(payoutRequestDto.getCustomer());
                String cardData = objectMapper.writeValueAsString(payoutRequestDto.getCardData());

                return Mono.fromSupplier(() -> paymentRepository.save(TransactionEntity.builder()
                                .merchantId(merchantId)
                                .providerTransactionId(payoutRequestDto.getProviderTransactionId())
                                .method(payoutRequestDto.getPaymentMethod())
                                .amount(payoutRequestDto.getAmount())
                                .currencyId(payoutRequestDto.getCurrency().toUpperCase())
                                .createdAt(payoutRequestDto.getCreatedAt())
                                .updatedAt(payoutRequestDto.getUpdatedAt())
                                .cardData(cardData)//here need parse card data
                                .languageId(payoutRequestDto.getLanguage().toUpperCase())
                                .notificationUrl(payoutRequestDto.getNotificationUrl())
                                .customerData(customerData)//here need parse card data
                                .status("COMPLETED")
                                .build())
                        .doOnSuccess(el -> transactionId.set(el.getId()))
                        .then(update(accountEntity))
                        .then(notificationService.send())
                        .then(Mono.just(PayoutResponseDto.builder().payoutId(transactionId.get()).status(PayoutResponseDto.Statuses.COMPLETED).message("OK").build()))
                        .onErrorResume(ex -> {
                            if (ex instanceof DataAccessException) {
                                log.warn("ERR_SAVE_ACCESS {}", ex.getMessage(), ex);
                                return Mono.just(PayoutResponseDto.builder().status(PayoutResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_COMMON").build());
                            } else {
                                log.warn("ERR_SAVE_COMMON {}", ex.getMessage(), ex);
                                return Mono.just(PayoutResponseDto.builder().status(PayoutResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
                            }
                        }).toFuture().join());
            }
        }

        return Mono.just(PayoutResponseDto.builder().build());
    }

    private void check(PayoutRequestDto payoutRequestDto) {
        checkCountry(payoutRequestDto.getCustomer().getCountry());
        checkCurrency(payoutRequestDto.getCurrency());
        checkLanguage(payoutRequestDto.getLanguage());
    }

    private void checkCountry(String id) {
        if (countryRepository.findById(id.toUpperCase()).toFuture().join() == null) {
            throw new RuntimeException("Resource not found");
        }
    }

    private void checkCurrency(String id) {
        if (currencyRepository.findById(id.toUpperCase()).toFuture().join() == null) {
            throw new RuntimeException("Resource not found");
        }
    }

    private void checkLanguage(String id) {
        if (languageRepository.findById(id.toUpperCase()).toFuture().join() == null) {
            throw new RuntimeException("Resource not found");
        }
    }

    private Mono<Integer> update(AccountEntity accountEntity) {
        return client.sql(
                        "update accounts set " +
                                "deposit_amount = :deposit_amount ," +
                                "is_overdraft = :is_overdraft ," +
                                "updated_at = :updated_at " +
                                "where merchant_id = :merchant_id")
                .bind("merchant_id", accountEntity.getMerchantId())
                .bind("deposit_amount", accountEntity.getDepositAmount())
                .bind("is_overdraft", accountEntity.getIsOverdraft())
                .bind("updated_at", accountEntity.getUpdatedAt())
                .fetch().rowsUpdated().flatMap(rowUpdated -> {
                    if (rowUpdated == 0) {
                        return Mono.error(new IllegalStateException("no update on id=" + accountEntity.getMerchantId()));
                    } else {
                        return Mono.error(new IllegalStateException("no update on id=" + accountEntity.getMerchantId()));
                    }
                });
    }
}
