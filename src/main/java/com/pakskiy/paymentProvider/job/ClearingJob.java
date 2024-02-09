package com.pakskiy.paymentProvider.job;

import com.pakskiy.paymentProvider.service.ClearingService;
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
public class ClearingJob {
    @Value("${app.clearingTickSeconds}")
    private long CLEARING_TICK;

    private final ClearingService clearingService;

    @PostConstruct
    public void init() {
        Flux.interval(Duration.ofSeconds(5), Duration.ofSeconds(CLEARING_TICK))
                .publishOn(Schedulers.newSingle("clearing-thread"))
                .map(tick -> clearingService.clear())
                .subscribe(
                        it -> log.info("Scheduled task executed at: {} ", java.time.LocalTime.now()),
                        error -> log.error("TIMER IS SHUTDOWN BECAUSE SEVERE ERROR ", error)
                );
    }
}
