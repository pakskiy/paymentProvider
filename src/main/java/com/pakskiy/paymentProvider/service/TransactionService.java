package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import com.pakskiy.paymentProvider.dto.TransactionType;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TransactionService {
    Mono<Long> process(TransactionRequestDto request, ServerWebExchange exchange, TransactionType type);

    Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate, ServerWebExchange exchange, TransactionType type);

    Mono<TransactionEntity> get(Long transactionId, TransactionType type);

    Mono<Void> validate(TransactionRequestDto request);
}