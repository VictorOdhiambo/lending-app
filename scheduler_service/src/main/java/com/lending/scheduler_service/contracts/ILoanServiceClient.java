package com.lending.scheduler_service.contracts;

import com.lending.scheduler_service.dto.LoanDTO;
import reactor.core.publisher.Flux;
import java.time.LocalDate;

/**
 * Client interface for interacting with the Loan Service.
 * Provides reactive methods to fetch loans based on various criteria.
 */
public interface ILoanServiceClient {

    /**
     * Retrieves all loans that are currently overdue.
     *
     * @return A Flux stream of overdue loan DTOs
     */
    Flux<LoanDTO> getOverdueLoans();

    /**
     * Retrieves all loans that are due on a specific date.
     *
     * @param dueDate The date on which the loans are due
     * @return A Flux stream of loan DTOs due on the specified date
     */
    Flux<LoanDTO> getLoansDueOn(LocalDate dueDate);

    /**
     * Retrieves all loans for a specific customer.
     *
     * @param customerId The ID of the customer
     * @return A Flux stream of loan DTOs belonging to the customer
     */
    Flux<LoanDTO> getLoansByCustomerId(String customerId);

    /**
     * Retrieves all loans that are at risk of default based on payment history.
     *
     * @return A Flux stream of at-risk loan DTOs
     */
    Flux<LoanDTO> getLoansAtRiskOfDefault();

    /**
     * Updates the status of a loan.
     *
     * @param loanId The ID of the loan
     * @param status The new status to set
     * @return A Flux containing the updated loan DTO
     */
    Flux<LoanDTO> updateLoanStatus(String loanId, String status);
}