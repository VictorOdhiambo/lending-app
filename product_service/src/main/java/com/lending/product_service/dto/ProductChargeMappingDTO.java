package com.lending.product_service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductChargeMappingDTO(
        UUID id,

        @NotNull(message = "Product Id is required")
        UUID productId,

        @NotNull(message = "Charge Id is required")
        UUID chargeId,

        LocalDateTime createdDate
) {
}
