package com.pakskiy.paymentProvider.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import com.pakskiy.paymentProvider.dto.TransactionStatus;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final MerchantServiceImpl merchantServiceImpl;
    private final CurrencyRepository currencyRepository;
    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;
    private final ObjectMapper objectMapper;
    private final TransactionalOperator transactionalOperator;
    private final NotificationService notificationService;

    @Override
    public Mono<Long> process(TransactionRequestDto request, ServerWebExchange exchange, TransactionType type) {
        return validate(request).then(Mono.defer(() -> accountServiceImpl.getById(exchange.getAttribute("merchantId"))
                        .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")))
                        .flatMap(account -> transactionRepository.save(getTransactionEntity(request, account.getMerchantId(), type, IN_PROGRESS)))
                        .map(transactionEntity -> Optional.ofNullable(transactionEntity.getId()).orElse(0L))))
                .onErrorResume(ex -> {
                    log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
                    return Mono.just(0L);
                });
    }

    @Override
    public Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate, ServerWebExchange exchange, TransactionType type) {
        return accountServiceImpl.getById(exchange.getAttribute("merchantId"))
                .switchIfEmpty(Mono.error(new RuntimeException("Account not founded"))).flux()
                .flatMap(account -> transactionRepository.findAllByAccountIdAndTypeEqualsAndCreatedAtBetweenOrderByCreatedAtDesc(account.getId(), type, startDate, endDate));
    }

    @Override
    public Mono<TransactionEntity> get(Long transactionId, TransactionType type, ServerWebExchange exchange) {
        return transactionRepository.findByIdAndTypeEqualsAndAccountId(transactionId, type, exchange.getAttribute("merchantId"));
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
    private TransactionEntity getTransactionEntity(TransactionRequestDto request, Long accountId, TransactionType type, TransactionStatus status) {
        String customerData = objectMapper.writeValueAsString(request.getCustomer());
        String cardData = objectMapper.writeValueAsString(request.getCardData());

        return TransactionEntity.builder().accountId(accountId).providerTransactionId(request.getProviderTransactionId()).method(request.getPaymentMethod()).amount(request.getAmount()).currencyId(request.getCurrency().toUpperCase()).createdAt(LocalDateTime.ofInstant(request.getCreatedAt().toInstant(), ZoneId.systemDefault())).updatedAt(LocalDateTime.ofInstant(request.getUpdatedAt().toInstant(), ZoneId.systemDefault())).cardData(cardData)//here need parse card data
                .languageId(request.getLanguage().toUpperCase()).notificationUrl(request.getNotificationUrl()).type(type).customerData(customerData)//here need parse card data
                .status(status).build();
    }

    public Mono<Void> check() {
        return transactionRepository.findAllByStatusEqualsOrderByCreatedAtAsc(IN_PROGRESS).collectSortedList().flatMap(transactionList -> {
            log.info("transactionList {}", transactionList);
            transactionList.stream().parallel().forEach(t -> checkTransaction(t).subscribe());
            return Mono.empty();
        });
        //switch on empty and doonerror
    }

    private Mono<Void> checkTransaction(TransactionEntity el) {
        transactionalOperator.transactional(Mono.defer(() -> accountServiceImpl.getById(el.getAccountId()).map(entity -> {
                    var currentDeposit = entity.getDepositAmount();
                    var currentLimit = entity.getLimitAmount();
                    var amount = el.getAmount();
                    log.info("entity {}", entity);
                    if (el.getType() == IN) {
                        entity.setDepositAmount(currentDeposit + amount);
                        el.setStatus(COMPLETED);
                    } else {
                        if ((currentDeposit + currentLimit) >= amount) {
                            entity.setDepositAmount(currentDeposit - amount);
                            el.setStatus(COMPLETED);
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
                //todo @TransactionalOperator for rollback
                .flatMap(one -> transactionRepository.save(el))).subscribe();
        return Mono.empty();
    }
}