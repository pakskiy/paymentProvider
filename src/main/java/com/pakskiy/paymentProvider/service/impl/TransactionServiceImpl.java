package com.pakskiy.paymentProvider.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import com.pakskiy.paymentProvider.dto.TransactionType;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.CountryRepository;
import com.pakskiy.paymentProvider.repository.CurrencyRepository;
import com.pakskiy.paymentProvider.repository.LanguageRepository;
import com.pakskiy.paymentProvider.repository.TransactionRepository;
import com.pakskiy.paymentProvider.service.NotificationService;
import com.pakskiy.paymentProvider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Optional;

import static com.pakskiy.paymentProvider.dto.TransactionStatus.COMPLETED;
import static com.pakskiy.paymentProvider.dto.TransactionStatus.FAILED;
import static com.pakskiy.paymentProvider.dto.TransactionStatus.IN_PROGRESS;
import static com.pakskiy.paymentProvider.dto.TransactionType.IN;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountServiceImpl accountServiceImpl;
    private final CurrencyRepository currencyRepository;
    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;
    private final ObjectMapper objectMapper;
    private final TransactionalOperator transactionalOperator;
    private final NotificationService notificationService;

    @Override
    public Mono<Long> process(TransactionRequestDto request, long accountId, TransactionType type) {
        return validate(request).then(Mono.defer(() -> accountServiceImpl.findById(accountId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")))
                        .flatMap(account -> transactionRepository.save(getTransactionEntity(request, account.getMerchantId(), type)))
                        .map(transactionEntity -> Optional.ofNullable(transactionEntity.getId()).orElse(0L))))
                .onErrorResume(ex -> {
                    log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
                    return Mono.just(0L);
                });
    }

    @Override
    public Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate, long accountId, TransactionType type) {
        return transactionRepository.findAllByAccountIdAndTypeEqualsAndCreatedAtBetweenOrderByCreatedAtDesc(accountId, type, startDate, endDate);
    }

    @Override
    public Mono<TransactionEntity> get(Long transactionId, TransactionType type, long accountId) {
        return transactionRepository.findByIdAndTypeEqualsAndAccountId(transactionId, type, accountId);
    }

    public Mono<Void> validate(TransactionRequestDto request) {
        return validateCountry(request.getCustomer().getCountry().toUpperCase()).then(validateCurrency(request.getCurrency().toUpperCase())).then(validateLanguage(request.getLanguage().toUpperCase()));
    }

    private Mono<Void> validateCountry(String id) {
        return countryRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Resource country not found"))).then();
    }

    private Mono<Void> validateCurrency(String id) {
        return currencyRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Resource currency not found"))).then();
    }

    private Mono<Void> validateLanguage(String id) {
        return languageRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Resource language not found"))).then();
    }

    @SneakyThrows
    private TransactionEntity getTransactionEntity(TransactionRequestDto request, Long accountId, TransactionType type) {
        String customerData = objectMapper.writeValueAsString(request.getCustomer());
        String cardData = objectMapper.writeValueAsString(request.getCardData());

        return TransactionEntity.builder().accountId(accountId)
                .providerTransactionId(request.getProviderTransactionId())
                .method(request.getPaymentMethod())
                .amount(request.getAmount())
                .currencyId(request.getCurrency().toUpperCase())
                .createdAt(LocalDateTime.ofInstant(request.getCreatedAt().toInstant(), ZoneId.systemDefault()))
                .updatedAt(LocalDateTime.ofInstant(request.getUpdatedAt().toInstant(), ZoneId.systemDefault()))
                .cardData(cardData)//here need parse card data
                .languageId(request.getLanguage().toUpperCase())
                .notificationUrl(request.getNotificationUrl())
                .type(type)
                .customerData(customerData)//here need parse card data
                .status(IN_PROGRESS).build();
    }

    public Mono<Void> check() {
        return transactionRepository.findAllByStatusEqualsOrderByCreatedAtAsc(IN_PROGRESS)
                .collectSortedList()
                .flatMap(transactionList -> {
            log.info("transactionList {}", transactionList);
            transactionList.stream().parallel().forEach(t -> checkTransaction(t).subscribe());
            return Mono.empty();
        });
        //switch on empty and doonerror
    }

    private Mono<Void> checkTransaction(TransactionEntity el) {
        transactionalOperator.transactional(Mono.defer(() -> accountServiceImpl.findById(el.getAccountId()).map(entity -> {
                    var currentDeposit = entity.getDepositAmount();
                    var currentLimit = entity.getLimitAmount();
                    var amount = el.getAmount();
                    log.info("entity {}", entity);

                    el.setStatus(COMPLETED);
                    el.setUpdatedAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

                    if (el.getType() == IN) {
                        entity.setDepositAmount(currentDeposit + amount);
                    } else {
                        if ((currentDeposit + currentLimit) >= amount) {
                            entity.setDepositAmount(currentDeposit - amount);
                        } else {
                            return Mono.error(new RuntimeException("Not enough deposit amount")).subscribe();
                        }
                    }
                    return accountServiceImpl.update(entity).then(notificationService.send(el)).subscribe();
                })).onErrorResume(ex -> {
                    log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
                    el.setStatus(FAILED);
                    return Mono.empty();
                })
                .flatMap(one -> transactionRepository.save(el))).subscribe();
        return Mono.empty();
    }
}