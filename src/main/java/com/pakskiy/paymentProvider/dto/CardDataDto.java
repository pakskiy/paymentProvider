package com.pakskiy.paymentProvider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDataDto {
    @NotNull
    @NotEmpty
    @JsonProperty("card_number")
    private String cardNumber;
    @NotNull
    @NotEmpty
    @JsonProperty("exp_date")
    private String expDate;
    @NotNull
    @NotEmpty
    @JsonProperty("cvv")
    private String cvv;
}