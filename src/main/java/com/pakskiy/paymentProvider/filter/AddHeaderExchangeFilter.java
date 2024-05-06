//package com.pakskiy.paymentProvider.filter;
//
//import jakarta.servlet.annotation.WebFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.ClientRequest;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.ExchangeFunction;
//import reactor.core.publisher.Mono;
//
//public class AddHeaderExchangeFilter implements ExchangeFilterFunction {
//    @Override
//    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
//
//        ClientRequest newRequest = ClientRequest.from(request)
//                .header("EFF-TEST-HEADER", "EFF-TEST-VALUE")
//                .build();
//        return next.exchange(newRequest);
//    }
//}
