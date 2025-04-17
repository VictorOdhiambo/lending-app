package com.lending.customer_service.controller;

import com.lending.customer_service.dto.CustomerDTO;
import com.lending.customer_service.service.contracts.ICustomerService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private Validator validator;
    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerDTO>> addCustomer(@RequestBody Mono<CustomerDTO> customerDTO) {
        return customerDTO.doOnNext(this::validate)
                .flatMap(customerService::addCustomer)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerDTO>> editCustomer(@PathVariable("id") String id,
            @RequestBody Mono<CustomerDTO> customerDTO) {
        return customerDTO
                .doOnNext(this::validate)
                .flatMap(customer -> customerService.editCustomer(UUID.fromString(id), customer))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<CustomerDTO>> findCustomerById(@PathVariable("id") String id){
        return ResponseEntity.ok(customerService.findCustomerById(UUID.fromString(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flux<CustomerDTO>> findAll(){
        return ResponseEntity.ok(customerService.findAll());
    }

    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
