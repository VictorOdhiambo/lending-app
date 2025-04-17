package com.lending.loan_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoanDTO(

        UUID id,

        @NotNull(message = "Product Id is required")
        UUID productId,

        @NotNull(message = "Customer Id is required")
        UUID customerId,

        String loanStatus,

        @NotNull
        @Positive(message = "Applied amount should be greater than zero")
        double appliedAmount,

        double interestAmount,

        double disbursementAmount,

        @NotNull
        @Positive(message = "Negotiated installment should be greater than zero")
        int negotiatedInstallment,

        @NotBlank(message = "Payment frequency is required")
        String paymentFrequency,

        LocalDateTime startDate,

        LocalDateTime dueDate,

        LocalDateTime endDate,

        LocalDateTime nextRepaymentDate,

        LocalDateTime disbursementDate,

        LocalDateTime clearedDate
) {
}
