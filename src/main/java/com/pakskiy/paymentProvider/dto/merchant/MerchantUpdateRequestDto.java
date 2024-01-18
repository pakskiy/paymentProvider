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
public class MerchantUpdateRequestDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("key")
    private String key;
    @JsonProperty("login")
    private String login;
}