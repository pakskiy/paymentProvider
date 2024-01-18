package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.CountryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends ReactiveCrudRepository<CountryEntity, String> {
}
