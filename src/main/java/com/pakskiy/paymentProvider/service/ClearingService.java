package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.TransactionStatus;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearingService {
    private final PaymentRepository paymentRepository;
    public Mono<Void> clear() {
        paymentRepository.findAllByStatusEqualsOrderByCreatedAt(TransactionStatus.IN_PROGRESS)
                .groupBy(TransactionEntity::getMerchantId)
                .parallel()
                .runOn(Schedulers.parallel()).flatMap(el -> clearTransaction(el));
        log.info("Void clear method");
        return Mono.empty();
    }

    private Publisher<?> clearTransaction(GroupedFlux<Long, TransactionEntity> el) {
    }


}
