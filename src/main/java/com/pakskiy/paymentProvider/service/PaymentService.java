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
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

import static com.pakskiy.paymentProvider.dto.TransactionStatus.FAILED;
import static com.pakskiy.paymentProvider.dto.TransactionStatus.IN_PROGRESS;
import static com.pakskiy.paymentProvider.dto.TransactionType.IN;

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

        return validate(request).then(Mono.defer(() -> {
            Mono<Long> transactionId = merchantService.getByToken(token)
                    .map(MerchantEntity::getId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Token not founded")))
                    .flatMap(accountService::getByMerchantId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")))
                    .flatMap(account -> paymentRepository.save(getTransactionEntity(request, account.getMerchantId())))
                    .map(transactionEntity -> Optional.ofNullable(transactionEntity.getId()).orElse(0L));

            return transactionId.map(res -> {
                if (res != null && res > 0) {
                    return PaymentResponseDto.builder().transactionId(res).status(IN_PROGRESS).message("OK").build();
                }
                return PaymentResponseDto.builder().status(FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build();
            });
        })).onErrorResume(ex -> {
            log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
            return Mono.just(PaymentResponseDto.builder().status(FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
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
                .type(IN)
                .customerData(customerData)//here need parse card data
                .status(IN_PROGRESS)
                .build();
    }

    private Mono<Void> validate(PaymentRequestDto paymentTransactionDto) {
        return validateCountry(paymentTransactionDto.getCustomer().getCountry().toUpperCase())
                .then(validateCurrency(paymentTransactionDto.getCurrency().toUpperCase()))
                .then(validateLanguage(paymentTransactionDto.getLanguage().toUpperCase()));
    }

    private Mono<Void> validateCountry(String id) {
        return countryRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource country not found")))
                .then();
    }

    private Mono<Void> validateCurrency(String id) {
        return currencyRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource currency not found")))
                .then();
    }

    private Mono<Void> validateLanguage(String id) {
        return languageRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Resource language not found")))
                .then();
    }

    public Flux<PaymentResponseDto> report() {
//        paymentRepository.findAllById
        return Flux.just(PaymentResponseDto.builder().status(FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
    }
    

    public Publisher<Void> check() {
        return paymentRepository.findAllByStatusEqualsOrderByCreatedAtAsc(IN_PROGRESS)
                .groupBy(TransactionEntity::getMerchantId)
                .parallel()
                .runOn(Schedulers.parallel()).flatMap(this::checkTransaction).then();
    }

    private Mono<Void> checkTransaction(GroupedFlux<Long, TransactionEntity> el) {
        return el.map(transactionEntity -> {
            log.info("Transaction data {}", transactionEntity);

            accountService.getByMerchantId(transactionEntity.getMerchantId()).switchIfEmpty(Mono.error(new RuntimeException("Account not founded")))
                    .mapNotNull(entity -> {
                        if (entity.getDepositAmount() <= transactionEntity.getAmount()) {
                            entity.setDepositAmount(entity.getDepositAmount() - transactionEntity.getAmount());
                            return entity;
                        }
                        return null;
                    }).onErrorResume(ex -> {
                        log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
                        return Mono.empty();
                    }).flatMap(account -> paymentRepository.save(account));


            account.map(acc -> {
                if (acc != null) {
                    return paymentRepository.save(account);
                }
            });

            if (account != null) {
                return paymentRepository.save(account);
            }


            if (account.getDepositAmount() <= transactionEntity.getAmount()) {
                account.setDepositAmount(account.getDepositAmount() - transactionEntity.getAmount());
                paymentRepository.save(account);
                return account;
            }
            ;

                        .flatMap(account -> {
                if (account.getDepositAmount() <= transactionEntity.getAmount()) {
                    account.setDepositAmount(account.getDepositAmount() - transactionEntity.getAmount());
                    paymentRepository.save(account);
                    return account;
                }
                ;
            });
            return Mono.empty().then();
        });
        return Mono.empty().then();


        map(transactionEntity -> {
            log.info("Transaction data {}", transactionEntity);

            accountService.getByMerchantId(transactionEntity.getMerchantId())
                    .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")))
                    .flatMap(account -> account.getDepositAmount()).flatMap()
            if (account.getDepositAmount() <= transactionEntity.getAmount()) {
                account.setDepositAmount(account.getDepositAmount() - transactionEntity.getAmount());
                return paymentRepository.save(account);
            }
            return account;
        })
    }).

    then();

//            return accountEntity.flatMap(account -> {
//                if(account.getDepositAmount() <= transactionEntity.getAmount()) {
//                    account.setDepositAmount(account.getDepositAmount() -transactionEntity.getAmount());
//                    paymentRepository.save(account).then();
//                }
//                return accountEntity;
//            });
//
//            paymentRepository.save()
//
//            return transactionEntity;
}
}
