package com.pakskiy.paymentProvider.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakskiy.paymentProvider.dto.TransactionResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseDto extends TransactionResponseDto {
    @JsonProperty("transactionId")
    private Long transactionId;
}