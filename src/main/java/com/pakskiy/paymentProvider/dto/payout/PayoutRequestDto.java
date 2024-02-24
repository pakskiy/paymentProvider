package com.pakskiy.paymentProvider.dto.payout;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakskiy.paymentProvider.dto.CardDataDto;
import com.pakskiy.paymentProvider.dto.CustomerDto;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class PayoutRequestDto {
    @JsonProperty("payout_id")
    private long payoutId;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("amount")
    private long amount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("provider_transaction_id")
    private String providerTransactionId;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    @JsonProperty("card_data")
    private CardDataDto cardData;
    @JsonProperty("language")
    private String language;
    @JsonProperty("notification_url")
    private String notificationUrl;
    @JsonProperty("customer")
    private CustomerDto customer;
    @JsonProperty("message")
    private String message;
    @JsonProperty("status")
    private String status;
}
