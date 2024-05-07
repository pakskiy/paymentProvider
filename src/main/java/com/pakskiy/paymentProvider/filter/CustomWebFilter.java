package com.pakskiy.paymentProvider.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakskiy.paymentProvider.dto.merchant.MerchantRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        //serverWebExchange.getResponse().getHeaders().add("web-filter", "web-filter-test");
//        serverWebExchange.getRequest().getHeaders().add("web-filter", "web-filter-test");
        ServerHttpRequest request = serverWebExchange.getRequest();
        ObjectMapper mapper = new ObjectMapper();
        return serverWebExchange
                .getRequest()
                .getBody()
                .cache()
                .next()
                .flatMap(body -> {
                    try {
                        var merchant = mapper.readValue(body.asInputStream(), MerchantRequestDto.class);
                        return webFilterChain.filter(serverWebExchange);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
        //
//
//
//                    try {
//                        return Mono.just(mapper.readValue(body.asInputStream(), String.class));
//                    } catch (IOException e) {
//                        return Mono.error(e);
//                    }
//                );


//        return webFilterChain.filter(serverWebExchange);
    }
}
