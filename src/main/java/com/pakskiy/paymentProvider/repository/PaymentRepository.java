package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.TransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<TransactionEntity, Long> {

    Flux<TransactionEntity> findAllByCreatedAtBetween(Date starDate, Date endDate);
}
