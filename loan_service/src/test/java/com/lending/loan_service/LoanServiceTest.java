package com.lending.loan_service;

import com.lending.loan_service.dto.LoanDTO;
import com.lending.loan_service.exception.LoanException;
import com.lending.loan_service.mapper.LoanMapper;
import com.lending.loan_service.mapper.RepaymentRequestMapper;
import com.lending.loan_service.model.Loan;
import com.lending.loan_service.repository.ILoanRepository;
import com.lending.loan_service.repository.IRepaymentRequestRepository;
import com.lending.loan_service.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private ILoanRepository loanRepository;
    @Mock
    private IRepaymentRequestRepository repaymentRequestRepository;
    @Mock
    private LoanMapper loanMapper;
    @Mock
    private RepaymentRequestMapper repaymentRequestMapper;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;
    private LoanDTO loanDTO;
    private UUID loanId;
    private UUID customerId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        loanId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();

        loan = new Loan(loanId, productId, customerId, "OPEN", 1000, 100, 900, 0, 1,
                "MONTHLY", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2), LocalDateTime.now());

        loanDTO = new LoanDTO(loan.getId(), loan.getProductId(), loan.getCustomerId(),
                loan.getLoanStatus(), loan.getAppliedAmount(), loan.getInterestAmount(),
                loan.getDisbursementAmount(), loan.getRepaidAmount(), loan.getNegotiatedInstallment(),
                loan.getPaymentFrequency(), loan.getStartDate(), loan.getDueDate(), loan.getEndDate(),
                loan.getDisbursementDate(), loan.getClearedDate(), loan.getCreatedDate());
    }

    @Test
    void addLoan_success() {
        when(loanMapper.toEntity(loanDTO)).thenReturn(loan);
        when(loanRepository.save(loan)).thenReturn(Mono.just(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDTO);

        StepVerifier.create(loanService.addLoan(loanDTO))
                .expectNext(loanDTO)
                .verifyComplete();
    }

    @Test
    void addLoan_failure() {
        when(loanMapper.toEntity(loanDTO)).thenReturn(loan);
        when(loanRepository.save(loan)).thenReturn(Mono.empty());

        StepVerifier.create(loanService.addLoan(loanDTO))
                .expectErrorMatches(throwable ->
                        throwable instanceof LoanException &&
                                throwable.getMessage().equals("Error when creating loan"))
                .verify();
    }

    @Test
    void editLoan_success() {
        loan.setLoanStatus("OVERDUE");

        when(loanRepository.findById(loanId)).thenReturn(Mono.just(loan));
        when(loanRepository.save(loan)).thenReturn(Mono.just(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDTO);

        StepVerifier.create(loanService.editLoan(loanId.toString(), loanDTO))
                .expectNext(loanDTO)
                .verifyComplete();
    }

    @Test
    void findLoanById_success() {
        when(loanRepository.findById(loanId)).thenReturn(Mono.just(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDTO);

        StepVerifier.create(loanService.findLoanById(loanId))
                .expectNext(loanDTO)
                .verifyComplete();
    }

    @Test
    void findLoansByCustomerId_success() {
        when(loanRepository.findByCustomerId(customerId)).thenReturn(Flux.just(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDTO);

        StepVerifier.create(loanService.findLoansByCustomerId(customerId))
                .expectNext(loanDTO)
                .verifyComplete();
    }

    @Test
    void findDueLoans_success() {
        loan.setDueDate(LocalDateTime.now());

        when(loanRepository.findAll()).thenReturn(Flux.just(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDTO);

        StepVerifier.create(loanService.findDueLoans())
                .expectNext(loanDTO)
                .verifyComplete();
    }

    @Test
    void findOverdueLoans_success() {
        loan.setDueDate(LocalDateTime.now().minusDays(1));

        when(loanRepository.findAll()).thenReturn(Flux.just(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDTO);

        StepVerifier.create(loanService.findOverdueLoans())
                .expectNext(loanDTO)
                .verifyComplete();
    }
}
