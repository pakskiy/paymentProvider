package com.pakskiy.paymentProvider.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@Table(name = "transactions")
public final class TransactionEntity {
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
    @Column(value = "created_at") private Date createdAt;
    @Column(value = "updated_at") private Date updatedAt;
    @Column(value = "status") private String status;
}
