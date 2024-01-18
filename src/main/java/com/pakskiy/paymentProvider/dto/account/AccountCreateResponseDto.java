package com.pakskiy.paymentProvider.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreateResponseDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("deposit_amount")
    private long depositAmount;
    @JsonProperty("limit_amount")
    private long limitAmount;
    @JsonProperty("error_code")
    private String errorCode;
}
