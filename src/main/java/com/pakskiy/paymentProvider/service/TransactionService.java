package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import com.pakskiy.paymentProvider.dto.TransactionType;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TransactionService {
    Mono<Long> process(TransactionRequestDto request, String token, TransactionType type);

    Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate, TransactionType type);

    Mono<TransactionEntity> get(Long transactionId, TransactionType type);

    Mono<Void> validate(TransactionRequestDto request);
}