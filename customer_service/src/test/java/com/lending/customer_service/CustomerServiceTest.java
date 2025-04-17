package com.lending.customer_service;

import com.lending.customer_service.dto.CustomerDTO;
import com.lending.customer_service.mapper.CustomerMapper;
import com.lending.customer_service.model.Customer;
import com.lending.customer_service.repository.ICustomerRepository;
import com.lending.customer_service.service.CustomerService;
import com.lending.customer_service.utils.LoanLimitScoring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private LoanLimitScoring loanLimitScoring;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setFirstName("Victor");
        customer.setLastName("Omondi");
        customer.setEmail("v.omondi.ke@gmail.com");
        customer.setIdentityNumber("ID12345");
        customer.setPhoneNumber("0712345678");
        customer.setDob(LocalDate.of(1995, 1, 1));
        customer.setMaxQualifiedAmount(50000.0);

        customerDTO = new CustomerDTO(customer.getId(),
                customer.getFirstName(), customer.getLastName(), customer.getIdentityNumber(),
                customer.getPhoneNumber(), customer.getEmail(), customer.getDob(), 0, customer.getMaxQualifiedAmount(), LocalDateTime.now());
    }

    @Test
    void addCustomer_shouldReturnSavedCustomerDTO() {
        when(customerMapper.toEntity(customerDTO)).thenReturn(customer);
        when(loanLimitScoring.scoreBasedOnAge(customerDTO)).thenReturn(50000.0);
        when(customerRepository.save(customer)).thenReturn(Mono.just(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDTO);

        StepVerifier.create(customerService.addCustomer(customerDTO))
                .expectNext(customerDTO)
                .verifyComplete();

        verify(customerRepository).save(customer);
        verify(loanLimitScoring).scoreBasedOnAge(customerDTO);
    }

    @Test
    void editCustomer_shouldUpdateAndReturnCustomerDTO() {
        when(customerRepository.findById(customerId)).thenReturn(Mono.just(customer));
        when(customerRepository.save(any())).thenReturn(Mono.just(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDTO);

        StepVerifier.create(customerService.editCustomer(customerId, customerDTO))
                .expectNext(customerDTO)
                .verifyComplete();

        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(any());
    }

    @Test
    void findCustomerById_shouldReturnCustomerDTO() {
        when(customerRepository.findById(customerId)).thenReturn(Mono.just(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDTO);

        StepVerifier.create(customerService.findCustomerById(customerId))
                .expectNext(customerDTO)
                .verifyComplete();

        verify(customerRepository).findById(customerId);
    }

    @Test
    void findAll_shouldReturnListOfCustomers() {
        when(customerRepository.findAll()).thenReturn(Flux.just(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDTO);

        StepVerifier.create(customerService.findAll())
                .expectNext(customerDTO)
                .verifyComplete();

        verify(customerRepository).findAll();
    }
}

