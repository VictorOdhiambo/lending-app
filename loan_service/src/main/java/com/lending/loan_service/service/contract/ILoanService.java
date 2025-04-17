package com.lending.loan_service.service.contract;

import com.lending.loan_service.dto.LoanDTO;
import com.lending.loan_service.dto.RepaymentRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ILoanService {
    /**
     * Creates a new loan
     * @param loanDTO
     * @return loanDto
     */
    Mono<LoanDTO> addLoan(LoanDTO loanDTO);

    /**
     * Creates a repayment request for an active loan
     * @param repaymentRequestDTO
     * @return repayment request dto
     */
    Mono<RepaymentRequestDTO> addRepayment(RepaymentRequestDTO repaymentRequestDTO);

    /**
     * Updates loan status
     * @param loanDTO
     * @return loanDto
     */
    Mono<LoanDTO> editLoan(String loanId, LoanDTO loanDTO);

    /**
     * Retrieve loan by id
     * @param loanId
     * @return loanDto
     */
    Mono<LoanDTO> findLoanById(UUID loanId);

    /**
     * Retrieve all loans for a given customer
     * @param customerId
     * @return Flux<LoanDTO>
     */
    Flux<LoanDTO> findLoansByCustomerId(UUID customerId);

    /**
     * Retrieve all loans due for payment - factors in grace period
     * @return Flux<LoanDTO>
     */
    Flux<LoanDTO> findDueLoans();

    /**
     * Retrieve all loans past due date
     * @return Flux<LoanDTO>
     */
    Flux<LoanDTO> findOverdueLoans();
}
