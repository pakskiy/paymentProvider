package com.pakskiy.paymentProvider.dto.payout;

import com.pakskiy.paymentProvider.dto.TransactionRequestDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
public class PayoutRequestDto extends TransactionRequestDto {
}
