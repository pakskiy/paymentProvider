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
public class TransactionCustomerDto {
    @NotNull
    @NotEmpty
    @JsonProperty("first_name")
    private String firstName;
    @NotNull
    @NotEmpty
    @JsonProperty("last_name")
    private String lastName;
    @NotNull
    @NotEmpty
    @JsonProperty("country")
    private String country;
}