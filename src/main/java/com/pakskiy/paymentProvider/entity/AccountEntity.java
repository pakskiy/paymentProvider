package com.pakskiy.paymentProvider.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table(name = "accounts")
public class AccountEntity implements Persistable<Long> {
    @Id
    @Column(value = "id")
    private Long id;
    @Column(value = "merchant_id")
    private Long merchantId;
    @Column(value = "deposit_amount")
    private Long depositAmount;
    @Column(value = "limit_amount")
    private Long limitAmount;
    @Column(value = "is_overdraft")
    private int isOverdraft;
    @Column(value = "created_at")
    private LocalDateTime createdAt;
    @Column(value = "updated_at")
    private LocalDateTime updatedAt;
    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
