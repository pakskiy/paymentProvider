package com.pakskiy.paymentProvider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private final ObjectMapper objectMapper;

    public Mono<PaymentResponseDto> create(PaymentRequestDto request, String token) {

        return check(request).then(Mono.defer(() -> {
            Mono<Long> transactionId = merchantService.getByToken(token)
                    .map(MerchantEntity::getId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Token not founded")))
                    .flatMap(accountService::getByMerchantId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")))
                    .flatMap(account -> paymentRepository.save(getTransactionEntity(request, account.getMerchantId())))
                    .map(transactionEntity -> transactionEntity.getId());

            return transactionId.map(res -> {
                if (res != null && res > 0) {
                    return PaymentResponseDto.builder().transactionId(res).status(PaymentResponseDto.Statuses.IN_PROCESS).message("OK").build();
                }
                return PaymentResponseDto.builder().status(PaymentResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build();
            });
        })).onErrorResume(ex -> {
            log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
            return Mono.just(PaymentResponseDto.builder().status(PaymentResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
        });
    }

    @SneakyThrows
    private TransactionEntity getTransactionEntity(PaymentRequestDto request, Long merchantId) {
        String customerData = objectMapper.writeValueAsString(request.getCustomer());
        String cardData = objectMapper.writeValueAsString(request.getCardData());

        return TransactionEntity.builder()
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
                .type("PAYMENT")
                .customerData(customerData)//here need parse card data
                .status("IN_PROCESS")
                .build();
    }

    private Mono<Void> check(PaymentRequestDto paymentTransactionDto) {
        return checkCountry(paymentTransactionDto.getCustomer().getCountry().toUpperCase())
                .then(checkCurrency(paymentTransactionDto.getCurrency().toUpperCase()))
                .then(checkLanguage(paymentTransactionDto.getLanguage().toUpperCase()));
    }

    private Mono<Void> checkCountry(String id) {
        return countryRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource country not found")))
                .then();
    }

    private Mono<Void> checkCurrency(String id) {
        return currencyRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource country not found")))
                .then();

    }

    private Mono<Void> checkLanguage(String id) {
        return languageRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource country not found")))
                .then();
    }

    public Flux<PaymentResponseDto> report() {

//        paymentRepository.findAllById

        return Flux.just(PaymentResponseDto.builder().status(PaymentResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
    }
}
