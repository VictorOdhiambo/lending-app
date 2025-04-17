package com.lending.notification_service.shared;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class LoanCreatedEvent {
    private String id;
    private String customerId;
    private double amount;
    private Integer negotiatedInstallment;
    private String status;
    private String paymentFrequency;
    private LocalDateTime createdAt;

    public LoanCreatedEvent() {
    }

    public LoanCreatedEvent(String id, String customerId, double amount
            , Integer negotiatedInstallment, String paymentFrequency) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.negotiatedInstallment = negotiatedInstallment;
        this.status = "OPEN";
        this.paymentFrequency = paymentFrequency;
        this.createdAt = LocalDateTime.now();
    }

}
