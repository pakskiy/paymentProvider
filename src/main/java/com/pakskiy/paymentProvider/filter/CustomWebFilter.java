package com.pakskiy.paymentProvider.filter;

import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomWebFilter implements WebFilter {

    private final MerchantService merchantService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().getHeaders().add("web-filter", "web-filter-test");
        exchange.getRequest().getHeaders().add("web-filter", "web-filter-test");

//        var headers = exchange.getRequest().getHeaders();
//        var currentUrl = exchange.getRequest().getURI().getPath();
//
//        if (currentUrl.contains("/api/v1/accounts")) {
//            if (headers.containsKey("Authorization")) {
//                merchantService.findByToken(headers.get("Authorization").get(0))
//                        .map(MerchantEntity::getId)
//                        .switchIfEmpty(Mono.error(new RuntimeException("Token not founded")))
//                        .flatMap(el -> {
//                            log.info("eoeoeoeo");
////                            headers.add("merchantId", String.valueOf(el));
//                            exchange.getRequest().getHeaders().add("web-filter", "web-filter-test");
//                            return chain.filter(exchange);
//                        }).onErrorResume(ex -> {
//                            log.error("ERR_CREATE_COMMON {}", ex.getMessage(), ex);
//                            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
//                            return chain.filter(exchange);
//                        }).subscribe(
//                                it -> log.info("TIMER TICK AT {} END AT {}", it, LocalDateTime.now()),
//                                error -> log.error("TIMER IS SHUTDOWN BECAUSE SEVERE ERROR ", error));
//            } else {
//                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
//                return chain.filter(exchange);
//            }
//        }
        return chain.filter(exchange);
    }
}
