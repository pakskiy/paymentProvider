package com.pakskiy.paymentProvider.dto.payment;

import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentRequestDto extends TransactionRequestDto {
}