package com.pakskiy.paymentProvider.dto;

import java.time.LocalDateTime;

public class TransactionRequestDto {
    private String paymentMethod;
    private long amount;
    private String currency;
    private String providerTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CardDataDto cardData;
    private String language;
    private String notificationUrl;
    private CustomerDto customer;
}
