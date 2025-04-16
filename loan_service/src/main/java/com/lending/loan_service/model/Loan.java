package com.lending.loan_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("Loan")
public class Loan {
    @Id
    @Column("id")
    private UUID id;

    @Column("product_id")
    private UUID productId;

    @Column("customer_id")
    private UUID customerId;

    @Column("loan_status")
    private String loanStatus;

    @Column("applied_amount")
    private double appliedAmount;

    @Column("interest_amount")
    private double interestAmount;

    @Column("disbursement_amount")
    private double disbursementAmount;

    @Column("negotiated_installment")
    private int negotiatedInstallment;

    @Column("payment_frequency")
    private String paymentFrequency;

    @Column("start_date")
    private LocalDateTime startDate;

    @Column("due_date")
    private LocalDateTime dueDate;

    @Column("end_date")
    private LocalDateTime endDate;

    @Column("next_repayment_date")
    private LocalDateTime nextRepaymentDate;

    @Column("disbursement_date")
    private LocalDateTime disbursementDate;

    @CreatedDate
    @Column("cleared_date")
    private LocalDateTime clearedDate;
}
