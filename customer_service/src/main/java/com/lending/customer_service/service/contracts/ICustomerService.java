package com.lending.customer_service.service.contracts;

import com.lending.customer_service.dto.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ICustomerService {
    Mono<CustomerDTO> addCustomer(CustomerDTO customerDTO);
    Mono<CustomerDTO> editCustomer(UUID customerId, CustomerDTO customerDTO);
    Mono<CustomerDTO> findCustomerById(UUID customerId);
    Flux<CustomerDTO> findAll();
}
