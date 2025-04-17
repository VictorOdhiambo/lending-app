package com.lending.loan_service.shared;

import lombok.Getter;

@Getter
public enum PaymentFrequency {
    DAILY(1),
    WEEKLY(7),
    BI_WEEKLY(14),
    MONTHLY(28),
    QUARTERLY(90),
    SEMI_ANNUALLY(180),
    ANNUALLY(360);

    final int value;
    PaymentFrequency(int value){
        this.value = value;
    }

}
