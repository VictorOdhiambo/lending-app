package com.lending.customer_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "loan.scoring")
public class LoanScoringProperties {

    private List<Rule> rules;

    public static class Rule {
        private int minAge;
        private int maxAge;
        private Double limit;

        // Getters and setters
        public int getMinAge() { return minAge; }
        public void setMinAge(int minAge) { this.minAge = minAge; }

        public int getMaxAge() { return maxAge; }
        public void setMaxAge(int maxAge) { this.maxAge = maxAge; }

        public Double getLimit() { return limit; }
        public void setLimit(Double limit) { this.limit = limit; }
    }

    public List<Rule> getRules() { return rules; }
    public void setRules(List<Rule> rules) { this.rules = rules; }
}

