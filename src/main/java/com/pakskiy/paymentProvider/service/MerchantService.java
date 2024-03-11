package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.merchant.MerchantRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantResponseDto;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MerchantService {
    Mono<MerchantResponseDto> create(MerchantRequestDto request);

    Mono<MerchantResponseDto> update(MerchantRequestDto request);

    Mono<MerchantResponseDto> get(Long id);

    Mono<MerchantEntity> findByToken(String token);

    Flux<MerchantEntity> list();
}
