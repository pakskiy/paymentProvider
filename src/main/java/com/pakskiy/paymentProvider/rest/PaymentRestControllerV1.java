package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.pakskiy.paymentProvider.dto.TransactionStatus.FAILED;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentRestControllerV1 {
    private final PaymentService paymentService;

    @PostMapping(value = "/payment")
    public Mono<ResponseEntity<PaymentResponseDto>> create(@NotNull @NotEmpty ServerWebExchange exchange,
                                                           @RequestBody @Valid PaymentRequestDto paymentRequestDto) {
        return paymentService.create(paymentRequestDto, exchange)
                .map(res -> (res.getStatus() != FAILED ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/list")
    public Flux<TransactionEntity> list(@NotNull @NotEmpty ServerWebExchange exchange,
                                        @RequestParam(value = "start_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime startDate,
                                        @RequestParam(value = "end_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime endDate) {
        return paymentService.list(startDate, endDate, exchange);
    }

    @GetMapping(value = "/transaction/{transactionId}/details")
    public Mono<TransactionEntity> get(@NotNull @NotEmpty ServerWebExchange accountExchange, @PathVariable Long transactionId) {
        return paymentService.get(transactionId, accountExchange);
    }
}