package com.pakskiy.paymentProvider.dto.payout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayoutResponseDto {
    @JsonProperty("payout_id")
    private Long payoutId;
    @JsonProperty("status")
    private Statuses status;
    @JsonProperty("message")
    private String message;

    public enum Statuses {IN_PROGRESS, COMPLETED, FAILED}
}