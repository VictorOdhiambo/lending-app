package com.lending.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("Charge")
public class Charge {
    @Id
    @Column("id")
    private UUID id;

    @Column("name")
    private String name;

    @Column("is_enabled")
    private boolean isEnabled;

    @Column("is_upfront")
    private boolean isUpfront;

    @Column("is_penalty")
    private boolean isPenalty;

    @Column("amount")
    private double amount;

    @Column("created_date")
    private LocalDateTime createdDate;

}
