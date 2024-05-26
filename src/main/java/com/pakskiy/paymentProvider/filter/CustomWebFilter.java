package com.pakskiy.paymentProvider.filter;

import com.pakskiy.paymentProvider.service.AccountService;
import com.pakskiy.paymentProvider.service.MerchantService;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomWebFilter implements WebFilter {

    private final MerchantService merchantService;
    private final AccountService accountService;

    @NonNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        var headers = exchange.getRequest().getHeaders();
        var currentUrl = exchange.getRequest().getURI().getPath();

        String authorizationHeader = headers.getFirst("Authorization");

        if (currentUrl.contains("/api/v1/accounts")) {
            //Set to attribute merchantId
            return merchantService.findByToken(authorizationHeader)
                    .switchIfEmpty(Mono.error(new RuntimeException("Token not founded")))
                    .flatMap(merchant -> {
                        exchange.getAttributes().put("merchantId", merchant.getId());
                        return chain.filter(exchange);
                    }).onErrorResume(ex -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().writeWith(
                                Mono.just(exchange.getResponse().bufferFactory().wrap("Missing or empty credentials".getBytes()))
                        );
                    });
        } else if (currentUrl.contains("/api/v1/payments") || currentUrl.contains("/api/v1/payouts")) {
            //Set to attribute accountId
            return merchantService.findByToken(authorizationHeader)
                    .switchIfEmpty(Mono.error(new RuntimeException("Token not founded")))
                    .flatMap(merchant -> accountService.findByMerchantId(merchant.getId())
                            .flatMap(account -> {
                                exchange.getAttributes().put("accountId", account.getId());
                                return chain.filter(exchange);
                            }).onErrorResume(ex -> {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().writeWith(
                                        Mono.just(exchange.getResponse().bufferFactory().wrap("Missing or empty credentials".getBytes()))
                                );
                            }));
        }
        return chain.filter(exchange);
    }
}