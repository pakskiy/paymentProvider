package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.MerchantEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MerchantRepository extends ReactiveCrudRepository<MerchantEntity, Long> {
    Mono<MerchantEntity> findByLoginAndKey(String login, String key);
}
