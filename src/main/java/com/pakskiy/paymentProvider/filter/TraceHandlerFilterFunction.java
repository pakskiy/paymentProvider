package com.pakskiy.paymentProvider.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TraceHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> handlerFunction) {

        ServerRequest serverRequest = ServerRequest.from(request)
                .header("traceId", "FUNCTIONAL-TRACE-ID")
                .build();
        return handlerFunction.handle(serverRequest);
    }
}
