package com.pakskiy.paymentProvider.repository;

import com.pakskiy.paymentProvider.entity.NotificationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<NotificationEntity, Long> {
}
