package com.lending.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ProductChargeMapping")
public class ProductChargeMapping {
    @Id
    @Column("id")
    private UUID id;

    @Column("product_id")
    private UUID productId;

    @Column("charge_id")
    private UUID chargeId;

    @Column("created_date")
    private LocalDateTime createdDate;
}
