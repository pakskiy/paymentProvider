package com.pakskiy.paymentProvider.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table(name = "merchants")
public class MerchantEntity {
    @Id
    @Column(value = "id") private final Long id;
    @Column(value = "login") private String login;
    @Column(value = "key") private String key;
    @Column(value = "created_at") private LocalDateTime createdAt;
    @Column(value = "updated_at") private LocalDateTime updatedAt;
    @Column(value = "status") private String status;
}