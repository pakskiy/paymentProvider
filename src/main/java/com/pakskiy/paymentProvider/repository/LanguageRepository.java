package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.LanguageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends ReactiveCrudRepository<LanguageEntity, String> {
}
