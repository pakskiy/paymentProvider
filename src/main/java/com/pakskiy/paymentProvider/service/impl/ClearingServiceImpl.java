package com.pakskiy.paymentProvider.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearingServiceImpl {
    private final NotificationServiceImpl notificationServiceImpl;
    private final AccountServiceImpl accountServiceImpl;
//    public Mono<Void> clear() {
//        return paymentRepository.findAllByOrderByCreatedAt()
//                .groupBy(TransactionEntity::getAccountId)
//                .parallel()
//                .runOn(Schedulers.parallel()).flatMap(this::clearTransaction).then();
//    }
//
//    /*
//    * 1. Check if account exist
//    * 2. Check if transaction amount is equals or less when account deposit
//    * 3. If yes, then need minus amount from deposit and update account and transaction
//    * 4. If no, then need fail and update transaction
//    * 5. Try to send notification to user by notification_url
//    */
//    private Publisher<Void> clearTransaction(GroupedFlux<Long, TransactionEntity> el) {
//        return el.map(transactionEntity -> {
//            log.info("Transaction data {}", transactionEntity);
//
//            Mono<AccountEntity> accountEntity = accountService.getById(transactionEntity.getAccountId())
//                    .switchIfEmpty(Mono.error(new RuntimeException("Account not founded")));
//
//            Mono<Long> amount = accountEntity.map(res -> {
//                if(res.getDepositAmount() <= transactionEntity.getAmount()) {
//                    return res.getDepositAmount() - transactionEntity.getAmount();
//                }
//                return 0L;
//            });
//
//
//            return transactionEntity;
//        }).then();
//    }
}