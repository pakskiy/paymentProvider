package com.pakskiy.paymentProvider.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @Column(value = "id")
    private Long id;
    @Column(value = "transactionId")
    private Long transaction_id;
    @Column(value = "url")
    private String url;
    @Column(value = "response")
    private String response;
}