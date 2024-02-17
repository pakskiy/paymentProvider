package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.dto.TransactionStatus;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<TransactionEntity, Long> {

    Flux<TransactionEntity> findAllByCreatedAtBetween(Date starDate, Date endDate);
    Flux<TransactionEntity> findAllByOrderByCreatedAt();
    Flux<TransactionEntity> findAllByStatusEqualsOrderByCreatedAtAsc(TransactionStatus status);
}
