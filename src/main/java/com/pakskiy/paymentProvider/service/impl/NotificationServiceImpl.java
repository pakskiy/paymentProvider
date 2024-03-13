package com.pakskiy.paymentProvider.service.impl;

import com.pakskiy.paymentProvider.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {
    private final NotificationRepository notificationRepository;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    //webclient not restclient
    public Mono<Void> send() {
        return retryTemplate.execute(retryContext -> webClient.method(HttpMethod.POST)
                .uri("your_post_endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("your_request_body"))
                .retrieve()
                .onStatus(httpStatusCode -> HttpStatus.ACCEPTED.is5xxServerError(), response -> Mono.error(new RuntimeException("Server error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10)) // Timeout after 10 seconds
                .doOnError(error -> System.out.println("Error occurred: " + error.getMessage())).then());
    }
}