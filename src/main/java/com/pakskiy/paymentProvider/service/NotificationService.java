package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Mono<Void> send() {

        return Mono.empty();
    }
}
