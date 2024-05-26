package com.pakskiy.paymentProvider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class TransactionResponseDto {
    @JsonProperty("status")
    private TransactionStatus status;
    @JsonProperty("message")
    private String message;

}
