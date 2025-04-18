package com.lending.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDTO(

        UUID id,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Identity number is required")
        String identityNumber,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotNull(message = "DOB is required")
        LocalDate dob,

        @NotNull
        @Positive(message = "Pre-qualified amount must be greater than 0")
        double prequalifiedAmount,

        @NotNull
        @Positive(message = "Max qualified amount must be greater than 0")
        double maxQualifiedAmount,

        LocalDateTime createdDate
) {}
