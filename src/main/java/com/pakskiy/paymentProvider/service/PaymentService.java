package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface PaymentService {
    Mono<PaymentResponseDto> create(PaymentRequestDto request, ServerWebExchange exchange);

    Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate, ServerWebExchange exchange);

    Mono<TransactionEntity> get(Long transactionId, ServerWebExchange exchange);
}
