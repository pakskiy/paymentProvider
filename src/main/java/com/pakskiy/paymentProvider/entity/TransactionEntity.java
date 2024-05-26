package com.pakskiy.paymentProvider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.pakskiy.paymentProvider.dto.TransactionStatus;
import com.pakskiy.paymentProvider.dto.TransactionType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table(name = "transactions")
public class TransactionEntity implements Persistable<Long>, Comparable<TransactionEntity> {
    @Id
    @Column(value = "id")
    private Long id;
    @Column(value = "amount")
    private Long amount;
    @Column(value = "method")
    private String method;
    @Column(value = "account_id")
    private Long accountId;
    @Column(value = "currency_id")
    private String currencyId;
    @Column(value = "provider_transaction_id")
    private String providerTransactionId;
    @Column(value = "card_data")
    private String cardData;
    @Column(value = "language_id")
    private String languageId;
    @Column(value = "notification_url")
    private String notificationUrl;
    @Column(value = "customer_data")
    private String customerData;
    @Column(value = "type")
    private TransactionType type; //IN-payment or OUT-payout
    @Column(value = "created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
    @Column(value = "updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedAt;
    @Column(value = "status")
    private TransactionStatus status;

    @Override
    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public int compareTo(TransactionEntity other) {
        return Long.compare(this.id, other.id);
    }
}
