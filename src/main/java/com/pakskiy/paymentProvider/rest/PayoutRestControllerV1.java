package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.dto.payout.PayoutRequestDto;
import com.pakskiy.paymentProvider.dto.payout.PayoutResponseDto;
import com.pakskiy.paymentProvider.service.PayoutService;
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
@RequestMapping("/api/v1/payouts")
@AllArgsConstructor
public class PayoutRestControllerV1 {
    private final PayoutService payoutService;

    @PostMapping(value = "/payout")
    public Mono<ResponseEntity<PayoutResponseDto>> create(@RequestHeader("Authorization") @NotNull @NotEmpty String token,
                                                          @RequestBody @Valid PayoutRequestDto payoutRequestDto) {
        return payoutService.create(payoutRequestDto, token)
                .map(res -> (res.getStatus() != FAILED ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }
}