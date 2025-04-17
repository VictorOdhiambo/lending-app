package com.lending.loan_service.repository;

import com.lending.loan_service.model.RepaymentRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface IRepaymentRequestRepository extends ReactiveCrudRepository<RepaymentRequest, UUID> {
}
