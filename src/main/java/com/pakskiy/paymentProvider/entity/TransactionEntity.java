package com.pakskiy.paymentProvider.entity;

import com.pakskiy.paymentProvider.dto.TransactionStatus;
import com.pakskiy.paymentProvider.dto.TransactionType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@Table(name = "transactions")
public final class TransactionEntity implements Persistable<Long> {
    @Id
    @Column(value = "id") private Long id;
    @Column(value = "amount") private Long amount;
    @Column(value = "method") private String method;
    @Column(value = "merchant_id") private Long merchantId;
    @Column(value = "currency_id") private String currencyId;
    @Column(value = "provider_transaction_id") private String providerTransactionId;
    @Column(value = "card_data") private String cardData;
    @Column(value = "language_id") private String languageId;
    @Column(value = "notification_url") private String notificationUrl;
    @Column(value = "customer_data") private String customerData;
    @Column(value = "type") private TransactionType type; //IN-payment or OUT-payout
    @Column(value = "created_at") private LocalDateTime createdAt;
    @Column(value = "updated_at") private LocalDateTime updatedAt;
//    @Column(value = "status") private String status;
    @Column(value = "status") private TransactionStatus status;

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
