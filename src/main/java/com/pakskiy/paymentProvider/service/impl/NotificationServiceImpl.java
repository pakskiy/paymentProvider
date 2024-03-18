package com.pakskiy.paymentProvider.service.impl;

import com.pakskiy.paymentProvider.entity.NotificationEntity;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.NotificationRepository;
import com.pakskiy.paymentProvider.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final WebClient webClient;

    public Mono<Void> send(TransactionEntity transaction) {
        String requestBody = "{\"transactionId\": \"" + transaction.getId() + "\", \"status\"" + transaction.getStatus() + "}";

        return webClient.post()
                .uri(transaction.getNotificationUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .maxBackoff(Duration.ofSeconds(10))
                        .doBeforeRetry(signal -> {
                            log.error("Retrying after error: " + signal);
                            saveNotification(transaction.getId(), transaction.getNotificationUrl(), "Retry " + signal.totalRetries() + " Error: " + signal.failure().getMessage());
                        })
                )
                .doOnSuccess(success -> saveNotification(transaction.getId(), transaction.getNotificationUrl(), "200OK"))
                .doOnError(error -> log.error("Fail request")).then();
    }

    private void saveNotification(Long transactionId, String url, String response) {
        notificationRepository.save(NotificationEntity.builder()
                .transactionId(transactionId)
                .url(url)
                .response(response)
                .build()).subscribe();
    }
}