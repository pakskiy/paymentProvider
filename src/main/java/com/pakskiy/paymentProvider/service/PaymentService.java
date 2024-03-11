package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface PaymentService {
    Mono<PaymentResponseDto> create(PaymentRequestDto request, String token);

    Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate);

    Mono<TransactionEntity> get(Long transactionId);
}
