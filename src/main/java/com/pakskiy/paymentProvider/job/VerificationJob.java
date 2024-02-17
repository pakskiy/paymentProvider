package com.pakskiy.paymentProvider.job;

import com.pakskiy.paymentProvider.service.ClearingService;
import com.pakskiy.paymentProvider.service.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

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
        clearing();
        checking();
    }

    private void clearing(){
        Flux.interval(Duration.ofSeconds(3), Duration.ofSeconds(CLEARING_STEP))
                .publishOn(Schedulers.newSingle("clearing-thread"))
                .flatMap(tick -> clearingService.clear())
                .subscribe(
                        it -> log.info("Scheduled clearing task executed at: {} ", java.time.LocalTime.now()),
                        error -> log.error("TIMER IS SHUTDOWN BECAUSE SEVERE ERROR ", error)
                );
    }
    private void checking(){
        Flux.interval(Duration.ofSeconds(5), Duration.ofSeconds(TRANSACTION_STEP))
                .publishOn(Schedulers.newSingle("checking-thread"))
                .flatMap(tick -> paymentService.check())
                .subscribe(
                        it -> log.info("Scheduled checking task executed at: {} ", java.time.LocalTime.now()),
                        error -> log.error("TIMER IS SHUTDOWN BECAUSE SEVERE ERROR ", error)
                );
    }
}
