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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public Mono<ResponseEntity<PaymentResponseDto>> create(@RequestHeader("Authorization") @NotNull @NotEmpty String token,
                                                           @RequestBody @Valid PaymentRequestDto paymentRequestDto) {
        return paymentService.create(paymentRequestDto, token)
                .map(res -> (res.getStatus() != FAILED ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/list")
    public Flux<TransactionEntity> list(@RequestParam("start_date") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime startDate,
                                        @RequestParam("end_date") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime endDate) {
        return paymentService.list(startDate, endDate);
    }

    @GetMapping(value = "/transaction/{transactionId}/details")
    public Mono<TransactionEntity> get(@PathVariable Long transactionId) {
        return paymentService.get(transactionId);
    }
}