package com.pakskiy.paymentProvider.service.impl;

import com.pakskiy.paymentProvider.dto.account.AccountRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import com.pakskiy.paymentProvider.repository.AccountRepository;
import com.pakskiy.paymentProvider.service.AccountService;
import com.pakskiy.paymentProvider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final MerchantService merchantService;
    private final static DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public Mono<AccountEntity> findById(Long id) {
        return accountRepository.findById(id)
                .doOnError(error -> log.error("Error occurred while finding entity with ID: {}", id, error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Entity with ID {} not found", id);
                    return Mono.empty();
                }));
    }

    public Mono<AccountEntity> findByMerchantId(Long id) {
        return accountRepository.findByMerchantId(id)
                .doOnError(error -> log.error("Error occurred while finding entity with ID: {}", id, error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Account with merchant id {} not found", id);
                    return Mono.empty();
                }));
    }

    @SneakyThrows
    public Mono<AccountResponseDto> create(AccountRequestDto request, ServerWebExchange exchange) {
        return accountRepository.save(AccountEntity.builder().merchantId(exchange.getAttribute("merchantId"))
                        .depositAmount(request.getDepositAmount())
                        .limitAmount(request.getLimitAmount()).isOverdraft(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build())
                .map(el -> AccountResponseDto.builder().id(el.getId()).depositAmount(el.getDepositAmount())
                        .limitAmount(el.getLimitAmount()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DuplicateKeyException) {
                        log.warn("ERR_SAVE_DUPLICATE {}", ex.getMessage(), ex);
                        return Mono.just(AccountResponseDto.builder()
                                .errorCode("-1001").build());
                    } else if (ex instanceof DataAccessException) {
                        log.warn("ERR_SAVE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(AccountResponseDto.builder()
                                .errorCode("-1002").build());
                    } else {
                        log.warn("ERR_SAVE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(AccountResponseDto.builder()
                                .errorCode("-1003").build());
                    }
                });

    }

    public Mono<AccountResponseDto> get(ServerWebExchange exchange) {
        return findByMerchantId(exchange.getAttribute("merchantId"))
                .map(account -> AccountResponseDto.builder().id(account.getId()).merchantId(account.getMerchantId())
                        .depositAmount(account.getDepositAmount()).limitAmount(account.getLimitAmount())
                        .isOverdraft(account.getIsOverdraft())
                        .createdAt(account.getCreatedAt().format(ISO_FORMATTER))
                        .updatedAt(account.getUpdatedAt().format(ISO_FORMATTER))
                        .build())
                .onErrorResume(ex -> {
                    if (ex instanceof DataAccessException) {
                        log.warn("ERR_UPDATE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(AccountResponseDto.builder()
                                .errorCode("-1007").build());
                    } else {
                        log.warn("ERR_UPDATE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(AccountResponseDto.builder()
                                .errorCode("-1008").build());
                    }
                });
    }

    public Mono<AccountEntity> update(AccountEntity account) {
        return accountRepository.save(account);
    }

    public Flux<AccountEntity> list() {
        return accountRepository.findAllByOrderById()
                .collectList().flatMapMany(account -> {
                    if (account.isEmpty()) {
                        return Flux.empty();
                    } else {
                        return Flux.fromIterable(account);
                    }
                });
    }
}
