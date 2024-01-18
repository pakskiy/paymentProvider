package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {
    Mono<AccountEntity> findByMerchantId(Long mid);
}
