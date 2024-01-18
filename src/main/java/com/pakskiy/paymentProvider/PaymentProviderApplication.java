package com.pakskiy.paymentProvider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableR2dbcRepositories
@EnableTransactionManagement
public class PaymentProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentProviderApplication.class, args);
    }
}