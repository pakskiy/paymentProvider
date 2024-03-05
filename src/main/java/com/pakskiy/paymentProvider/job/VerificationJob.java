package com.pakskiy.paymentProvider.job;

import com.pakskiy.paymentProvider.service.ClearingService;
import com.pakskiy.paymentProvider.service.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationJob {
    @Value("${app.checkTransactionStepInSeconds}")
    private long TRANSACTION_STEP;

    @Value("${app.clearingStepInSeconds}")
    private long CLEARING_STEP;

    private final ClearingService clearingService;
    private final PaymentService paymentService;

    @PostConstruct
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
    private void checking(){
        Flux.interval(Duration.ofSeconds(3), Duration.ofSeconds(TRANSACTION_STEP))
                .publishOn(Schedulers.newSingle("trx-checking-thread"))
                .flatMap(i -> {
                    LocalDateTime now = LocalDateTime.now();
                    return paymentService.check().then(Mono.just(now))
                            .doOnError(ex ->
                                    log.error("ERROR IN TICK", ex)).
                            onErrorReturn(now);
                }).doOnError(ex -> {
                    log.error("TIMER SHUTDOWN BECAUSE ERROR IN timerSource", ex);
                })
                .doOnSubscribe( s -> {
                    log.info("TIMER STARTED");
                })
                .repeat()
                .subscribe(
                        it -> log.info("TIMER TICK AT {} END AT {}", it, LocalDateTime.now()),
                        error -> log.error("TIMER IS SHUTDOWN BECAUSE SEVERE ERROR ", error));
    }
}