package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.dto.payment.PaymentRequestDto;
import com.pakskiy.paymentProvider.dto.payment.PaymentResponseDto;
import com.pakskiy.paymentProvider.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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

//    @PostMapping(value = "/payment")
//    public Mono<PaymentResponseDto> create(@RequestBody PaymentRequestDto paymentTransactionDto) {
//        return paymentService.create(paymentTransactionDto).flatMap(result -> {
//            if (result.getStatus() == APPROVED) {
//                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                        .body(BodyInserters.fromValue(result), PaymentResponseDto.class);
//            } else {
//                return ResponseEntity.badRequest().body(result);
//            }
//        });
//    }
}

