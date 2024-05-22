package com.pakskiy.paymentProvider.filter;

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

    @NonNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        var headers = exchange.getRequest().getHeaders();
        var currentUrl = exchange.getRequest().getURI().getPath();

        String authorizationHeader = headers.getFirst("Authorization");

        if (!currentUrl.contains("/api/v1/accounts") && !currentUrl.contains("/api/v1/payments")) {
            return chain.filter(exchange);
        }

        return merchantService.findByToken(authorizationHeader)
                .switchIfEmpty(Mono.error(new RuntimeException("Token not founded")))
                .flatMap(merchant -> {
                    exchange.getAttributes().put("merchantId", merchant.getId());
                    return chain.filter(exchange);
                }).onErrorResume(ex -> {
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return chain.filter(exchange);
                });
    }
}
