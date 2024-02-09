package com.pakskiy.paymentProvider.dto.payout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakskiy.paymentProvider.dto.TransactionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayoutResponseDto {
    @JsonProperty("payout_id")
    private Long payoutId;
    @JsonProperty("status")
    private TransactionStatus status;
    @JsonProperty("message")
    private String message;
}