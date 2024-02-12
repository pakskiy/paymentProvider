package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.TransactionStatus;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearingService {
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    public Mono<Void> clear() {
        return paymentRepository.findAllByOrderByCreatedAt()
                .groupBy(TransactionEntity::getMerchantId)
                .parallel()
                .runOn(Schedulers.parallel()).flatMap(this::clearTransaction).then();
    }

    private Publisher<?> clearTransaction(GroupedFlux<Long, TransactionEntity> el) {
        return el.map(transactionEntity -> {
            log.info("Transaction data {}", transactionEntity);
            return transactionEntity;
        }).then();
    }
}