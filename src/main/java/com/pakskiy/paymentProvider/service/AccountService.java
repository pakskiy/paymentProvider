package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.account.AccountRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountEntity> findById(Long id);

    Mono<AccountEntity> findByMerchantId(Long id);

    Mono<AccountResponseDto> create(AccountRequestDto request, ServerWebExchange exchange);

    Mono<AccountResponseDto> get(ServerWebExchange exchange);

    Mono<AccountEntity> update(AccountEntity account);

    Flux<AccountEntity> list();
}
