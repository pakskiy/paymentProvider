package com.pakskiy.paymentProvider.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("deposit_amount")
    private Long depositAmount;
    @JsonProperty("limit_amount")
    private Long limitAmount;
    @JsonProperty("is_overdraft")
    private int isOverdraft;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("error_code")
    private String errorCode;
}
