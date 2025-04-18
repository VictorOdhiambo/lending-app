package com.lending.customer_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "loan.scoring")
public class LoanScoringProperties {

    private List<Rule> rules;

    @Setter
    @Getter
    public static class Rule {
        // Getters and setters
        private int minAge;
        private int maxAge;
        private Double limit;

    }

}

