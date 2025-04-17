package com.lending.customer_service.utils;


import com.lending.customer_service.config.LoanScoringProperties;
import com.lending.customer_service.dto.CustomerDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class LoanLimitScoring {

    private final LoanScoringProperties scoringProperties;

    public LoanLimitScoring(LoanScoringProperties scoringProperties) {
        this.scoringProperties = scoringProperties;
    }

    public Double scoreBasedOnAge(CustomerDTO customer) {
        if (customer.dob() == null) {
            throw new IllegalArgumentException("Date of Birth must not be null.");
        }

        int age = Period.between(customer.dob(), LocalDate.now()).getYears();

        return scoringProperties.getRules().stream()
                .filter(rule -> age >= rule.getMinAge() && age <= rule.getMaxAge())
                .map(LoanScoringProperties.Rule::getLimit)
                .findFirst()
                .orElse(Double.valueOf("0"));
    }
}
