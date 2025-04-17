package com.lending.notification_service.model;

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
@Table("Notification")
public class Notification {
    @Id
    @Column("id")
    private UUID id;

    @Column("customer_id")
    private UUID customerId;

    @Column("loan_id")
    private UUID loanId;

    @Column("type")
    private String type;

    @Column("message")
    private String message;

    @Column("status")
    private String status;

    @Column("created_date")
    private LocalDateTime createdDate;
}
