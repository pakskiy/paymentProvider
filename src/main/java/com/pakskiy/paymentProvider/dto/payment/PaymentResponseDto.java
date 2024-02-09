package com.pakskiy.paymentProvider.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakskiy.paymentProvider.dto.TransactionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseDto {
    @JsonProperty("transaction_id")
    private final Long transactionId;
    @JsonProperty("status")
    private final TransactionStatus status;
    @JsonProperty("message")
    private String message;
}