package com.pakskiy.paymentProvider.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "countries")
public class CountryEntity {
    @Id
    @Column(value = "id")
    private String id;
    @Column(value = "name")
    private String name;
}
