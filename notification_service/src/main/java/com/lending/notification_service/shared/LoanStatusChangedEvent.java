package com.lending.notification_service.shared;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class LoanStatusChangedEvent implements Serializable {
    // Getters and Setters
    private String loanId;
    private String customerId;
    private String message;
    private String newStatus;
    private LocalDateTime updatedAt;

    public LoanStatusChangedEvent() {
    }

    public LoanStatusChangedEvent(String loanId, String customerId, String message, String newStatus) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.message = message;
        this.newStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

}
