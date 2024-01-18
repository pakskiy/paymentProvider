package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.TransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayoutRepository extends ReactiveCrudRepository<TransactionEntity, Long> {
}
