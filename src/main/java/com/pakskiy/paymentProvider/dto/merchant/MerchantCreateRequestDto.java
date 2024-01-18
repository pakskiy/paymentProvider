package com.pakskiy.paymentProvider.dto.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCreateRequestDto {
    @JsonProperty("login")
    private String login;
    @JsonProperty("key")
    private String key;
}
