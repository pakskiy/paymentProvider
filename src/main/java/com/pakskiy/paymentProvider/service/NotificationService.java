package com.pakskiy.paymentProvider.service;

import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<Void> send();
}
