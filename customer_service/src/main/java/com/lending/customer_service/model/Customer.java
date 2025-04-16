package com.lending.customer_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {
    @Id
    @Column("id")
    private UUID id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("identity_number")
    private String identityNumber;

    @Column("phone_number")
    private String phoneNumber;

    @Column("email")
    private String email;

    @Column("prequalified_amount")
    private double prequalifiedAmount;

    @Column("max_qualified_amount")
    private double maxQualifiedAmount;

    @Column("created_date")
    private LocalDateTime createdDate;
}
