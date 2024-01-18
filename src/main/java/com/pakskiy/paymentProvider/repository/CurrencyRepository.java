package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.CurrencyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends ReactiveCrudRepository<CurrencyEntity, String> {
}
