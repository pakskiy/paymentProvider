package com.pakskiy.paymentProvider.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    @Bean
    TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

    @Bean
    ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(logRequest())
                .build();
    }

    @Bean
    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> System.out.println(name + ": " + value)));
            return Mono.just(clientRequest);
        });
    }

//    @Bean
//    public WebFilter addCustomRequestHeaderFilter() {
//        return (exchange, chain) -> {
////            // Modify request headers here
////            ServerWebExchange modifiedExchange = exchange.mutate()
////                    .request(builder -> builder.header("foo", "header value"))
////                    .build();
////
////            // Proceed with the chain
////            return chain.filter(modifiedExchange);
//            ServerHttpRequest request = exchange.getRequest();
//            HttpHeaders headers = request.getHeaders();
//            Flux<DataBuffer> body = request.getBody();
//
//            // Transform Flux<DataBuffer> to a Mono<String> representing the request body
//            Mono<String> bodyMono = body
//                    .map(dataBuffer -> dataBuffer.toString(StandardCharsets.UTF_8))
//                    .reduce(String::concat);
//            log.info("WebFilter here");
//            // Process request body
////            return bodyMono.flatMap(requestBody -> {
////                // Find element or perform any operation on the request body
////                if (requestBody.contains("searchElement")) {
////                    // Log or perform operation
////                    log.info("Element found in request body");
////                }
////
////                // Proceed with the filter chain
////                return chain.filter(exchange);
////            });
//            return chain.filter(exchange);
//        };

//    }

//    @Bean
//    FilterRegistrationBean<HeaderOverrideFilter> headerOerrideFilterRegistration() {
//
//        FilterRegistrationBean<HeaderOverrideFilter> filterRegistrationBean = new FilterRegistrationBean<>(
//                new HeaderOverrideFilter());
//
//        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
//
//        /*
//         * -------------------------------------------------------------------------------------------------------
//         * This is extremely important. We want this filter to register itself even before any Tomcat filters.
//         * -------------------------------------------------------------------------------------------------------
//         */
//        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//
//        log.info("Configured HeaderOverrideFilter ***********************************************");
//
//        return filterRegistrationBean;
//    }

}
