package com.pakskiy.paymentProvider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.merchant.MerchantCreateResponseDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.AccountRepository;
import com.pakskiy.paymentProvider.repository.CountryRepository;
import com.pakskiy.paymentProvider.repository.CurrencyRepository;
import com.pakskiy.paymentProvider.repository.LanguageRepository;
import com.pakskiy.paymentProvider.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
    private final AccountRepository accountRepository;

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
        })).onErrorResume(ex -> Mono.just(PaymentResponseDto.builder().status(PaymentResponseDto.Statuses.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build()));
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

//    @SneakyThrows
//    private Mono<Long> save(Long merchantId, PaymentRequestDto request) {
//        check(request);
//
//        Optional<AccountEntity> accountEntityOptional = accountService.getByMerchantId(merchantId);
//
//        if (accountEntityOptional.isEmpty()) {
//            throw new RuntimeException();
//        }
//
//        long newDeposit = accountEntityOptional.get().getDepositAmount() + request.getAmount();
//        AtomicLong transactionId = new AtomicLong(0L);
//
//        AccountEntity accountEntity = AccountEntity.builder()
//                .merchantId(merchantId).depositAmount(newDeposit)
//                .limitAmount(accountEntityOptional.get().getLimitAmount())
//                .isOverdraft(accountEntityOptional.get().getIsOverdraft())
//                .createdAt(accountEntityOptional.get().getCreatedAt())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        String customerData = objectMapper.writeValueAsString(request.getCustomer());
//        String cardData = objectMapper.writeValueAsString(request.getCardData());
//
//
//        return transactionalOperator.transactional(
//                Mono.defer(() ->
//                        saveTransaction(TransactionEntity.builder()
//                                .merchantId(merchantId)
//                                .providerTransactionId(request.getProviderTransactionId())
//                                .method(request.getPaymentMethod())
//                                .amount(request.getAmount())
//                                .currencyId(request.getCurrency().toUpperCase())
//                                .createdAt(request.getCreatedAt())
//                                .updatedAt(request.getUpdatedAt())
//                                .cardData(cardData)//here need parse card data
//                                .languageId(request.getLanguage().toUpperCase())
//                                .notificationUrl(request.getNotificationUrl())
//                                .customerData(customerData)//here need parse card data
//                                .status("COMPLETED")
//                                .build())
//                                .flatMap(el -> Mono.just(el.getId()))
//                                .flatMap(tuple1 -> Mono.just(Tuples.of(saveAccount(accountEntity), tuple1)))
//                                .flatMap(tuple2 -> Mono.just(Tuples.of(paymentRepository.save(TransactionEntity.builder().id(tuple2.getT2()).status("COMPLETED").build()), tuple2.getT2())))
//                                .map(el -> el.getT2())));

//                        .map(TransactionEntity::getId).subscribe()


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
//        return transactionId.get();
//    }

//    private Mono<TransactionEntity> saveTransaction(TransactionEntity transactionEntity) {
//        return paymentRepository.save(transactionEntity);
//    }

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
