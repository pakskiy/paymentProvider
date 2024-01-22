package com.pakskiy.paymentProvider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
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
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CountryRepository countryRepository;
    private final AccountService accountService;
    private final CurrencyRepository currencyRepository;
    private final LanguageRepository languageRepository;
    private final MerchantService merchantService;
    private final NotificationService notificationService;
    private final DatabaseClient client;
    private final ObjectMapper objectMapper;
    private final TransactionalOperator transactionalOperator;

    public Mono<PaymentResponseDto> create(PaymentRequestDto request, String token) {
        try {
            Optional<MerchantEntity> merchantEntityOptional = merchantService.getByToken(token);

            if (merchantEntityOptional.isEmpty()) {
                return Mono.just(PaymentResponseDto.builder().status(PaymentResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
            }

            Long merchantId = merchantEntityOptional.get().getId();

            Long transactionId = save(merchantId, request);

            if (transactionId > 0) {
                return Mono.just(PaymentResponseDto.builder().transactionId(transactionId).status(PaymentResponseDto.Statuses.APPROVED).message("OK").build());
            }
        } catch (Exception e) {
            log.error("ERR_CREATE {}", e.getMessage(), e);
        }
        return Mono.just(PaymentResponseDto.builder().status(PaymentResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
    }

    private void check(PaymentRequestDto paymentTransactionDto) {
        checkCountry(paymentTransactionDto.getCustomer().getCountry().toUpperCase());
        checkCurrency(paymentTransactionDto.getCurrency().toUpperCase());
        checkLanguage(paymentTransactionDto.getLanguage().toUpperCase());
    }

    private void checkCountry(String id) {
        if (!countryRepository.existsById(id).toFuture().join()) {
            throw new RuntimeException("Resource country not found");
        }
    }

    private void checkCurrency(String id) {
        if (!currencyRepository.existsById(id).toFuture().join()) {
            throw new RuntimeException("Resource currency not found");
        }

    }

    private void checkLanguage(String id) {
        if (!languageRepository.existsById(id).toFuture().join()) {
            throw new RuntimeException("Resource language not found");
        }
    }

    @SneakyThrows
    private Long save(Long merchantId, PaymentRequestDto request) {
        check(request);

        Optional<AccountEntity> accountEntityOptional = accountService.getByMerchantId(merchantId);

        if (accountEntityOptional.isEmpty()) {
            throw new RuntimeException();
        }

        long newDeposit = accountEntityOptional.get().getDepositAmount() + request.getAmount();
        AtomicLong transactionId = new AtomicLong(0L);

        AccountEntity accountEntity = AccountEntity.builder()
                .merchantId(merchantId).depositAmount(newDeposit)
                .limitAmount(accountEntityOptional.get().getLimitAmount())
                .isOverdraft(accountEntityOptional.get().getIsOverdraft())
                .createdAt(accountEntityOptional.get().getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        String customerData = objectMapper.writeValueAsString(request.getCustomer());
        String cardData = objectMapper.writeValueAsString(request.getCardData());

        transactionalOperator.transactional(Mono.defer(() ->
                saveTransaction(TransactionEntity.builder()
                        .merchantId(merchantId)
                        .providerTransactionId(request.getProviderTransactionId())
                        .method(request.getPaymentMethod())
                        .amount(request.getAmount())
                        .currencyId(request.getCurrency().toUpperCase())
                        .createdAt(request.getCreatedAt())
                        .updatedAt(request.getUpdatedAt())
                        .cardData(cardData)//here need parse card data
                        .languageId(request.getLanguage().toUpperCase())
                        .notificationUrl(request.getNotificationUrl())
                        .customerData(customerData)//here need parse card data
                        .status("COMPLETED")
                        .build()).map(el-> {
                    transactionId.set(el.getId());
                    System.out.println("transactionId: " + transactionId);
                    return el.getId();
                }).flatMap(saveId -> saveAccount(accountEntity)))).toFuture().join();

//        TransactionEntity transactionEntity = saveTransaction(TransactionEntity.builder()
//                .merchantId(merchantId)
//                .providerTransactionId(request.getProviderTransactionId())
//                .method(request.getPaymentMethod())
//                .amount(request.getAmount())
//                .currencyId(request.getCurrency().toUpperCase())
//                .createdAt(request.getCreatedAt())
//                .updatedAt(request.getUpdatedAt())
//                .cardData(cardData)//here need parse card data
//                .languageId(request.getLanguage().toUpperCase())
//                .notificationUrl(request.getNotificationUrl())
//                .customerData(customerData)//here need parse card data
//                .status("COMPLETED")
//                .build()).toFuture().join();

//        int updCnt = saveAccount(accountEntity).toFuture().join();
//
//        if (updCnt == 0) {
//            throw new RuntimeException();
//        }
        return transactionId.get();
    }

    private Mono<TransactionEntity> saveTransaction(TransactionEntity transactionEntity) {
        return paymentRepository.save(transactionEntity);
    }

    private Mono<Integer> saveAccount(AccountEntity accountEntity) {
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
                        return Mono.empty();
//                        return Mono.error(new IllegalStateException("no update on id=" + accountEntity.getMerchantId()));
                    }
                });
    }

    public Flux<PaymentResponseDto> report() {

//        paymentRepository.findAllById

        return Flux.just(PaymentResponseDto.builder().status(PaymentResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
    }
}
