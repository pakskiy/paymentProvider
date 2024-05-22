package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.dto.account.AccountRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import com.pakskiy.paymentProvider.service.AccountService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountRestControllerV1 {
    private final AccountService accountService;

    @PostMapping(value = "/create")
    public Mono<ResponseEntity<AccountResponseDto>> create(@RequestHeader("Authorization") @NotNull @NotEmpty String token
            , @NotNull @NotEmpty ServerWebExchange exchange, @RequestBody AccountRequestDto request) {
        return accountService.create(request, token)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/get")
    public Mono<ResponseEntity<AccountResponseDto>> get(@NotNull @NotEmpty ServerWebExchange exchange) {
        return accountService.get(exchange)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/list")
    public Flux<AccountEntity> list() {
        return accountService.list();
    }
}