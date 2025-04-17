package com.lending.loan_service.service;

import com.lending.loan_service.dto.LoanDTO;
import com.lending.loan_service.dto.RepaymentRequestDTO;
import com.lending.loan_service.exception.LoanException;
import com.lending.loan_service.exception.RepaymentRequestException;
import com.lending.loan_service.mapper.LoanMapper;
import com.lending.loan_service.mapper.RepaymentRequestMapper;
import com.lending.loan_service.model.Loan;
import com.lending.loan_service.model.RepaymentRequest;
import com.lending.loan_service.repository.ILoanRepository;
import com.lending.loan_service.repository.IRepaymentRequestRepository;
import com.lending.loan_service.service.contract.ILoanService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoanService implements ILoanService {
    private final LoanMapper loanMapper;
    private final RepaymentRequestMapper repaymentRequestMapper;
    private final ILoanRepository loanRepository;
    private final IRepaymentRequestRepository repaymentRequestRepository;

    public LoanService(ILoanRepository loanRepository, IRepaymentRequestRepository repaymentRequestRepository,
                       LoanMapper loanMapper, RepaymentRequestMapper repaymentRequestMapper) {
        this.loanRepository = loanRepository;
        this.repaymentRequestRepository = repaymentRequestRepository;
        this.loanMapper = loanMapper;
        this.repaymentRequestMapper = repaymentRequestMapper;
    }

    @Override
    public Mono<LoanDTO> addLoan(LoanDTO loanDTO) {
        Loan loan = loanMapper.toEntity(loanDTO);
        return loanRepository.save(loan)
                .map(loanMapper::toDto)
                .switchIfEmpty(Mono.error(new LoanException("Error when creating loan")));
    }

    @Override
    public Mono<RepaymentRequestDTO> addRepayment(RepaymentRequestDTO repaymentRequestDTO) {
        RepaymentRequest repaymentRequest = repaymentRequestMapper.toEntity(repaymentRequestDTO);
        return repaymentRequestRepository.save(repaymentRequest)
                .map(repaymentRequestMapper::toDto)
                .switchIfEmpty(Mono.error(new RepaymentRequestException("Error when creating repayment request")));
    }

    @Override
    public Mono<LoanDTO> editLoan(String loanId, LoanDTO loanDTO) {
        return loanRepository.findById(UUID.fromString(loanId))
                .switchIfEmpty(Mono.error(new LoanException("Error retrieving loan details")))
                .flatMap(loan -> {
                    loan.setLoanStatus(loanDTO.loanStatus());
                    loan.setDisbursementAmount(loanDTO.disbursementAmount());
                    loan.setInterestAmount(loanDTO.interestAmount());
                    return loanRepository.save(loan)
                            .map(loanMapper::toDto);
                });
    }

    @Override
    public Mono<LoanDTO> findLoanById(UUID loanId) {
        return loanRepository.findById(loanId)
                .switchIfEmpty(Mono.error(new LoanException("Error retrieving loan details")))
                .map(loanMapper::toDto);
    }

    @Override
    public Flux<LoanDTO> findLoansByCustomerId(UUID customerId) {
        return loanRepository.findByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new LoanException("Error retrieving loan details")))
                .map(loanMapper::toDto);
    }

    @Override
    public Flux<LoanDTO> findDueLoans() {
        LocalDateTime now = LocalDateTime.now();
        return loanRepository.findAll()
                .filter(loan -> loan.getDueDate().isEqual(now))
                .map(loanMapper::toDto)
                .switchIfEmpty(Mono.error(new LoanException("No due loans found")));
    }


    @Override
    public Flux<LoanDTO> findOverdueLoans() {
        LocalDateTime now = LocalDateTime.now();
        return loanRepository.findAll()
                .filter(loan -> loan.getDueDate().isBefore(now))
                .map(loanMapper::toDto)
                .switchIfEmpty(Mono.error(new LoanException("No due over loans found")));
    }
}
