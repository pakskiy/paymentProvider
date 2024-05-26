package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.dto.TransactionStatus;
import com.pakskiy.paymentProvider.dto.TransactionType;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<TransactionEntity, Long> {
    Flux<TransactionEntity> findAllByAccountIdAndTypeEqualsAndCreatedAtBetweenOrderByCreatedAtDesc(long accountId, TransactionType type, LocalDateTime startDate, LocalDateTime endDate);

    Mono<TransactionEntity> findByIdAndTypeEqualsAndAccountId(long transactionId, TransactionType type, long accountId);

    Flux<TransactionEntity> findAllByStatusEqualsOrderByCreatedAtAsc(TransactionStatus status);
}
