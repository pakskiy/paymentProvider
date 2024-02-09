package com.pakskiy.paymentProvider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.payout.PayoutRequestDto;
import com.pakskiy.paymentProvider.dto.payout.PayoutResponseDto;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.CountryRepository;
import com.pakskiy.paymentProvider.repository.CurrencyRepository;
import com.pakskiy.paymentProvider.repository.LanguageRepository;
import com.pakskiy.paymentProvider.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.pakskiy.paymentProvider.dto.TransactionStatus.FAILED;
import static com.pakskiy.paymentProvider.dto.TransactionStatus.IN_PROGRESS;
import static com.pakskiy.paymentProvider.dto.TransactionType.OUT;

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
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Transactional
    public Mono<PayoutResponseDto> create(PayoutRequestDto request, String token) {
        return check(request).then(Mono.defer(() -> {
            Mono<Long> transactionId = merchantService.getByToken(token)
                    .map(MerchantEntity::getId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Token not founded")))
                    .flatMap(accountService::getByMerchantId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")))
                    .flatMap(account -> paymentRepository.save(getTransactionEntity(request, account.getMerchantId())))
                    .map(transactionEntity -> Optional.ofNullable(transactionEntity.getId()).orElse(0L));

            return transactionId.map(res -> {
                if (res != null && res > 0) {
                    return PayoutResponseDto.builder().payoutId(res).status(IN_PROGRESS).message("OK").build();
                }
                return PayoutResponseDto.builder().status(FAILED).message("PAYOUT_METHOD_NOT_ALLOWED").build();
            });
        })).onErrorResume(ex -> {
            log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
            return Mono.just(PayoutResponseDto.builder().status(FAILED).message("PAYOUT_METHOD_NOT_ALLOWED").build());
        });
    }

    private Mono<Void> check(PayoutRequestDto payoutRequestDto) {
        return checkCountry(payoutRequestDto.getCustomer().getCountry().toUpperCase())
                .then(checkCurrency(payoutRequestDto.getCurrency().toUpperCase()))
                .then(checkLanguage(payoutRequestDto.getLanguage().toUpperCase()));
    }

    private Mono<Void> checkCountry(String id) {
        return countryRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource country not found")))
                .then();
    }

    private Mono<Void> checkCurrency(String id) {
        return currencyRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource currency not found")))
                .then();
    }

    private Mono<Void> checkLanguage(String id) {
        return languageRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource language not found")))
                .then();
    }

    @SneakyThrows
    private TransactionEntity getTransactionEntity(PayoutRequestDto request, Long merchantId) {
        String customerData = objectMapper.writeValueAsString(request.getCustomer());
        String cardData = objectMapper.writeValueAsString(request.getCardData());

        return TransactionEntity.builder()
                .merchantId(merchantId)
                .providerTransactionId(request.getProviderTransactionId())
                .method(request.getPaymentMethod())
                .amount(-request.getAmount())
                .currencyId(request.getCurrency().toUpperCase())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .cardData(cardData)//here need parse card data
                .languageId(request.getLanguage().toUpperCase())
                .notificationUrl(request.getNotificationUrl())
                .type(OUT)
                .customerData(customerData)//here need parse card data
                .status(IN_PROGRESS)
                .build();
    }
}