package com.lending.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChargeDTO(

        UUID id,

        @NotBlank(message = "Name is required")
        String name,

        boolean isEnabled,

        boolean isUpfront,

        boolean isPenalty,

        @NotNull
        @Positive(message = "Amount must be greater than 0")
        double amount,

        LocalDateTime createdDate
) {}
