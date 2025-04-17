package com.lending.customer_service.repository;

import com.lending.customer_service.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ICustomerRepository extends ReactiveCrudRepository<Customer, UUID> {
}
