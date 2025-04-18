package com.lending.customer_service.service;

import com.lending.customer_service.dto.CustomerDTO;
import com.lending.customer_service.exception.CustomerException;
import com.lending.customer_service.mapper.CustomerMapper;
import com.lending.customer_service.model.Customer;
import com.lending.customer_service.repository.ICustomerRepository;
import com.lending.customer_service.service.contracts.ICustomerService;
import com.lending.customer_service.shared.CustomerCreatedEvent;
import com.lending.customer_service.utils.LoanLimitScoring;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public class CustomerService implements ICustomerService {
    private final ICustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final LoanLimitScoring loanLimitScoring;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routing-key.customer-created}")
    private String customerStatusKey;

    public CustomerService(ICustomerRepository customerRepository, RabbitTemplate rabbitTemplate,
                           CustomerMapper customerMapper, LoanLimitScoring loanLimitScoring) {
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.customerMapper = customerMapper;
        this.loanLimitScoring = loanLimitScoring;
    }

    @Override
    public Mono<CustomerDTO> addCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        // score customer
        Double loanLimit = loanLimitScoring.scoreBasedOnAge(customerDTO);
        customer.setMaxQualifiedAmount(loanLimit);
        return customerRepository.save(customer)
                .map(customerMapper::toDto)
                .switchIfEmpty(Mono.error(new CustomerException("Error when creating customer")))
                .doOnSuccess(savedCustomer -> {
                    CustomerCreatedEvent event = new CustomerCreatedEvent(
                            savedCustomer.id().toString(),
                            savedCustomer.firstName(),
                            savedCustomer.email()
                    );
                    rabbitTemplate.convertAndSend(notificationExchange, customerStatusKey, event);
                });
    }

    @Override
    public Mono<CustomerDTO> editCustomer(UUID customerId, CustomerDTO customerDTO) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerException("Error retrieving customer details")))
                .flatMap(customer -> {
                    customer.setFirstName(customerDTO.firstName());
                    customer.setLastName(customerDTO.lastName());
                    customer.setEmail(customerDTO.email());
                    customer.setIdentityNumber(customerDTO.identityNumber());
                    customer.setPhoneNumber(customerDTO.phoneNumber());
                    customer.setMaxQualifiedAmount(customerDTO.maxQualifiedAmount());
                    customer.setDob(customerDTO.dob());

                    return customerRepository.save(customer)
                            .doOnSuccess(
                                    savedCustomer -> {
                                        CustomerCreatedEvent event = new CustomerCreatedEvent(
                                                savedCustomer.getId().toString(),
                                                savedCustomer.getFirstName() + " " + savedCustomer.getFirstName(),
                                                savedCustomer.getEmail()
                                        );
                                        rabbitTemplate.convertAndSend(notificationExchange, customerStatusKey, event);
                                    }
                            ).map(customerMapper::toDto);
                });

    }

    @Override
    public Mono<CustomerDTO> findCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerException("Error retrieving customer details")))
                .map(customerMapper::toDto);
    }

    @Override
    public Flux<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .switchIfEmpty(Mono.error(new CustomerException("Error retrieving customers")))
                .map(customerMapper::toDto);
    }
}
