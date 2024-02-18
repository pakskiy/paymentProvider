package com.pakskiy.paymentProvider.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakskiy.paymentProvider.dto.TransactionCardDataDto;
import com.pakskiy.paymentProvider.dto.TransactionCustomerDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentRequestDto {
    @NotNull
    @NotEmpty
    @JsonProperty("payment_method")
    private String paymentMethod;
    @NotNull
    @Positive
    @JsonProperty("amount")
    private long amount;
    @NotNull
    @NotEmpty
    @JsonProperty("currency")
    private String currency;
    @NotNull
    @JsonProperty("provider_transaction_id")
    private String providerTransactionId;
    @NotNull
    @JsonProperty("createdAt")
    private Date createdAt;
    @NotNull
    @JsonProperty("updatedAt")
    private Date updatedAt;
    @NotNull
    @JsonProperty("card_data")
    private TransactionCardDataDto cardData;
    @NotNull
    @NotEmpty
    @JsonProperty("language")
    private String language;
    @NotNull
    @NotEmpty
    @JsonProperty("notification_url")
    private String notificationUrl;
    @NotNull
    @JsonProperty("customer")
    private TransactionCustomerDto customer;
}