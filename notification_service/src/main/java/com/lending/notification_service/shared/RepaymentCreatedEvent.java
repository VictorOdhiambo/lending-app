package com.lending.notification_service.shared;

import java.time.LocalDateTime;

public class RepaymentCreatedEvent {
    private String id;
    private String loanId;
    private String customerId;
    private double amount;
    private LocalDateTime repaymentDate;

    // Constructors
    public RepaymentCreatedEvent() {
    }

    public RepaymentCreatedEvent(String id, String loanId, String customerId, double amount) {
        this.id = id;
        this.loanId = loanId;
        this.customerId = customerId;
        this.amount = amount;
        this.repaymentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(LocalDateTime repaymentDate) {
        this.repaymentDate = repaymentDate;
    }
}
