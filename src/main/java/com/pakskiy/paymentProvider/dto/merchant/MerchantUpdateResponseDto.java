package com.pakskiy.paymentProvider.dto.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantUpdateResponseDto {
    @JsonProperty("id") private Long id;
    @JsonProperty("login") private String login;
    @JsonProperty("key") private String key;
    @JsonProperty("error_code") private String errorCode;
}
