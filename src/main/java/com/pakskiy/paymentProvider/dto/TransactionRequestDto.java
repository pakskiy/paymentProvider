package com.pakskiy.paymentProvider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionRequestDto {
    @NotNull
    @NotEmpty
    @JsonProperty("payment_method")
    private String paymentMethod;
    @NotNull
    @Positive
    private long amount;
    @NotNull
    @NotEmpty
    private String currency;
    @NotNull
    @JsonProperty("provider_transaction_id")
    private String providerTransactionId;
    @NotNull
    @JsonProperty("created_at")
    private Date createdAt;
    @NotNull
    @JsonProperty("updated_at")
    private Date updatedAt;
    @NotNull
    @JsonProperty("card_data")
    private CardDataDto cardData;
    @NotNull
    @NotEmpty
    private String language;
    @NotNull
    @NotEmpty
    @JsonProperty("notification_url")
    private String notificationUrl;
    @NotNull
    private CustomerDto customer;
}