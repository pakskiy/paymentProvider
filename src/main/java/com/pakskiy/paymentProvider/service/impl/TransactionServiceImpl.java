package com.pakskiy.paymentProvider.service.impl;

import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import com.pakskiy.paymentProvider.dto.TransactionType;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.service.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class TransactionServiceImpl implements TransactionService {
    @Override
    public Mono<PaymentResponseDto> create(TransactionRequestDto request, TransactionType type, String token) {
        return null;
    }

    @Override
    public Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate, TransactionType type) {
        return null;
    }

    @Override
    public Mono<TransactionEntity> get(Long transactionId) {
        return null;
    }
}
