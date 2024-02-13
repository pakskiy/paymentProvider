package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.TransactionStatus;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import com.pakskiy.paymentProvider.entity.TransactionEntity;
import com.pakskiy.paymentProvider.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearingService {
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final AccountService accountService;
    public Mono<Void> clear() {
        return paymentRepository.findAllByOrderByCreatedAt()
                .groupBy(TransactionEntity::getMerchantId)
                .parallel()
                .runOn(Schedulers.parallel()).flatMap(this::clearTransaction).then();
    }

    /*
    * 1. Check if account exist
    * 2. Check if transaction amount is equals or less when account deposit
    * 3. If yes, then need minus amount from deposit and update account and transaction
    * 4. If no, then need fail and update transaction
    * 5. Try to send notification to user by notification_url
    */
    private Publisher<Void> clearTransaction(GroupedFlux<Long, TransactionEntity> el) {
        return el.map(transactionEntity -> {
            log.info("Transaction data {}", transactionEntity);

            Mono<AccountEntity> accountEntity = accountService.getByMerchantId(transactionEntity.getMerchantId())
                    .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")));

            Mono<Long> amount = accountEntity.map(res -> {
                if(res.getDepositAmount() <= transactionEntity.getAmount()) {
                    return res.getDepositAmount() - transactionEntity.getAmount();
                }
                return 0L;
            });


            return transactionEntity;
        }).then();
    }
}