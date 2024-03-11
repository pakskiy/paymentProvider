package com.pakskiy.paymentProvider.service.impl;

import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.service.PaymentService;
import com.pakskiy.paymentProvider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.pakskiy.paymentProvider.dto.TransactionStatus.FAILED;
import static com.pakskiy.paymentProvider.dto.TransactionStatus.IN_PROGRESS;
import static com.pakskiy.paymentProvider.dto.TransactionType.IN;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final TransactionService transactionService;

    @Transactional
    public Mono<PaymentResponseDto> create(PaymentRequestDto request, String token) {
        return transactionService.process(request, token, IN).map(res -> {
            if (res != null && res > 0) {
                return PaymentResponseDto.builder().transactionId(res).status(IN_PROGRESS).message("OK").build();
            }
            return PaymentResponseDto.builder().status(FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build();

        }).onErrorResume(ex -> {
            log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
            return Mono.just(PaymentResponseDto.builder().status(FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build());
        });
    }

    public Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionService.list(startDate, endDate, IN);
    }

    public Mono<TransactionEntity> get(Long transactionId) {
        return transactionService.get(transactionId, IN);
    }
}
