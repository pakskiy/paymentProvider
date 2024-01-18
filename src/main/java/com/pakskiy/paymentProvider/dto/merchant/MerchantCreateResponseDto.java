package com.pakskiy.paymentProvider.dto.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantCreateResponseDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("key")
    private String key;
    @JsonProperty("login")
    private String login;
    @JsonProperty("error_code")
    private String errorCode;
}
