package com.lending.notification_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDTO(
        UUID id,

        UUID customerId,

        UUID loanId,

        @NotBlank(message = "Notification type is required")
        String type,

        @NotBlank(message = "Message is required")
        String message,

        String status,

        LocalDateTime createdDate
) { }
