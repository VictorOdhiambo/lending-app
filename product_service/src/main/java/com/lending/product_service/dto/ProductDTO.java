package com.lending.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDTO(
        UUID id,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        boolean isEnabled,

        @NotNull(message = "Grace period is required")
        int gracePeriod,

        LocalDateTime createdDate
) {}

