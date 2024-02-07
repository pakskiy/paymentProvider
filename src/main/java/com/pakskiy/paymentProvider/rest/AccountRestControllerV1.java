package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.dto.account.AccountCreateRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountCreateResponseDto;
import com.pakskiy.paymentProvider.dto.account.AccountGetResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantCreateRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantCreateResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantGetResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantUpdateRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantUpdateResponseDto;
import com.pakskiy.paymentProvider.service.AccountService;
import com.pakskiy.paymentProvider.service.MerchantService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountRestControllerV1 {
    private final AccountService accountService;

    @PostMapping(value = "/create")
    public Mono<ResponseEntity<AccountCreateResponseDto>> create(@RequestHeader("Authorization") @NotNull @NotEmpty String token,
                                                                 @RequestBody AccountCreateRequestDto request) {
        return accountService.create(request, token)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/get")
    public Mono<ResponseEntity<AccountGetResponseDto>> get(@RequestHeader("Authorization") @NotNull @NotEmpty String token) {
        return accountService.get(token)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }
}