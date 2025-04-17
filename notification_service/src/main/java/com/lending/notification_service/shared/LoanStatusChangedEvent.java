package com.lending.notification_service.shared;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class LoanStatusChangedEvent {
    // Getters and Setters
    private String loanId;
    private String customerId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime updatedAt;

    public LoanStatusChangedEvent() {
    }

    public LoanStatusChangedEvent(String loanId, String customerId, String previousStatus, String newStatus) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

}
