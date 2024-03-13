package com.pakskiy.paymentProvider.job;

import com.pakskiy.paymentProvider.service.impl.ClearingServiceImpl;
import com.pakskiy.paymentProvider.service.impl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationJob {
    @Value("${app.clearingStepInSeconds}")
    private long CLEARING_STEP;

    private final ClearingServiceImpl clearingServiceImpl;
    private final TransactionServiceImpl transactionService;

    @Scheduled(fixedRateString = "${app.checkTransactionStepInSeconds}", initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void init() {
        //clearing();
        checking();
    }

    //    private void clearing(){ // DO I NEED CHECK TRANSACTIONS IN THIS TASK????
//        Flux.interval(Duration.ofSeconds(3), Duration.ofSeconds(CLEARING_STEP))
//                .publishOn(Schedulers.newSingle("clearing-thread"))
//                .flatMap(tick -> clearingService.clear())
//                .subscribe(
//                        it -> log.info("Scheduled clearing task executed at: {} ", java.time.LocalTime.now()),
//                        error -> log.error("TIMER IS SHUTDOWN BECAUSE SEVERE ERROR ", error)
//                );
//    }
    private void checking() {
        transactionService.check().then(Mono.just(LocalDateTime.now()))
                .doOnError(ex -> log.error("ERROR IN TICK", ex))
                .doOnSubscribe(s -> {
                    log.info("TIMER STARTED");
                })
                .subscribe(
                        it -> log.info("TIMER TICK AT {} END AT {}", it, LocalDateTime.now()),
                        error -> log.error("TIMER IS SHUTDOWN BECAUSE SEVERE ERROR ", error));

    }
}