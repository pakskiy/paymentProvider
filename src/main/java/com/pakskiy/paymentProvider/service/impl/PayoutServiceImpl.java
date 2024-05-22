package com.pakskiy.paymentProvider.service.impl;

import com.pakskiy.paymentProvider.dto.payout.PayoutRequestDto;
import com.pakskiy.paymentProvider.dto.payout.PayoutResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.service.PayoutService;
import com.pakskiy.paymentProvider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.pakskiy.paymentProvider.dto.TransactionStatus.FAILED;
import static com.pakskiy.paymentProvider.dto.TransactionStatus.IN_PROGRESS;
import static com.pakskiy.paymentProvider.dto.TransactionType.IN;
import static com.pakskiy.paymentProvider.dto.TransactionType.OUT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {
    private final TransactionService transactionService;

    @Transactional
    public Mono<PayoutResponseDto> create(PayoutRequestDto request, ServerWebExchange exchange) {
        return transactionService.process(request, exchange, OUT).map(res -> {
            if (res != null && res > 0) {
                return PayoutResponseDto.builder().transactionId(res).status(IN_PROGRESS).message("OK").build();
            }
            return PayoutResponseDto.builder().status(FAILED).message("PAYOUT_METHOD_NOT_ALLOWED").build();

        }).onErrorResume(ex -> {
            log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
            return Mono.just(PayoutResponseDto.builder().status(FAILED).message("PAYOUT_METHOD_NOT_ALLOWED").build());
        });
    }

    public Flux<TransactionEntity> list(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionService.list(startDate, endDate, IN);
    }

    public Mono<TransactionEntity> get(Long transactionId) {
        return transactionService.get(transactionId, OUT);
    }
}