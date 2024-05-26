package com.pakskiy.paymentProvider.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountRequestDto {
    @JsonProperty("deposit_amount")
    private long depositAmount;
    @JsonProperty("limit_amount")
    private long limitAmount;
}
