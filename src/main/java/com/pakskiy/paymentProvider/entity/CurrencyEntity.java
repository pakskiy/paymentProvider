package com.pakskiy.paymentProvider.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "currencies")
public class CurrencyEntity {
    @Id
    @Column(value = "id")
    private String id;
    @Column(value = "code")
    private String code;
    @Column(value = "name")
    private String name;
}
