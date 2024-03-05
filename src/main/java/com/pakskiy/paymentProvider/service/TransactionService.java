package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import com.pakskiy.paymentProvider.dto.TransactionType;
import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TransactionService {
    Mono<PaymentResponseDto> create(TransactionRequestDto request, TransactionType type, String token);

    Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate, TransactionType type);

    Mono<TransactionEntity> get(Long transactionId);
}
