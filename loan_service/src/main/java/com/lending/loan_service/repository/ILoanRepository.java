package com.lending.loan_service.repository;

import com.lending.loan_service.model.Loan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ILoanRepository extends ReactiveCrudRepository<Loan, UUID> {
    Flux<Loan> findByCustomerId(UUID customerId);
    Flux<Loan> findByLoanStatus(String status);
}
