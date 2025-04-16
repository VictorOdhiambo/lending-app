package com.lending.scheduler_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanDTO {
    private String id;
    private String customerName;
    private double amountDue;
    private LocalDateTime dueDate;
}
