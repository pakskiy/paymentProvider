package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.payout.PayoutRequestDto;
import com.pakskiy.paymentProvider.dto.payout.PayoutResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface PayoutService {
    Mono<PayoutResponseDto> create(PayoutRequestDto request, String token);

    Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate);

    Mono<TransactionEntity> get(Long transactionId);
}
