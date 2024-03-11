package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.account.AccountRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountEntity> getById(Long id);

    Mono<AccountResponseDto> create(AccountRequestDto request, String token);

    Mono<AccountResponseDto> get(String token);
    Mono<AccountEntity> update(AccountEntity account);

    Flux<AccountEntity> list();
}
