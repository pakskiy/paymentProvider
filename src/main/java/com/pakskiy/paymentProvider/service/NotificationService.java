package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.entity.TransactionEntity;
import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<Void> send(TransactionEntity transaction);
}
