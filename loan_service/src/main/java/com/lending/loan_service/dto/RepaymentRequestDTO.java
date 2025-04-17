package com.lending.loan_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record RepaymentRequestDTO(

        UUID id,

        @NotNull(message = "Customer Id is required")
        UUID customerId,

        @NotNull(message = "Loan Id is required")
        UUID loanId,

        @NotNull
        @Positive(message = "Amount should be greater than 0")
        double amount,

        String status,

        LocalDateTime createdDate
) {
}
