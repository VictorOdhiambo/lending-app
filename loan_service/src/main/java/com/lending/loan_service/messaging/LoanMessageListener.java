package com.lending.loan_service.messaging;

import com.lending.loan_service.dto.LoanDTO;
import com.lending.loan_service.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanMessageListener {

    private final LoanService loanService;

    @RabbitListener(queues = "${rabbitmq.queue.loan-processing}")
    public void processLoanApplication(String loanId) {
        log.info("Received loan processing request for ID: {}", loanId);
        loanService.processLoan(loanId)
                .doOnSuccess(loan -> log.info("Successfully processed loan: {}", loanId))
                .doOnError(e -> log.error("Error processing loan {}: {}", loanId, e.getMessage()))
                .subscribe();
    }

    @RabbitListener(queues = "${rabbitmq.queue.loan-approval}")
    public void handleLoanApproval(LoanDTO loanDTO) {
        log.info("Received loan approval decision for ID: {}", loanDTO.id());
        loanService.approveLoan(loanDTO)
                .doOnSuccess(loan -> log.info("Successfully applied approval decision for loan: {}", loanDTO.id()))
                .doOnError(e -> log.error("Error applying approval for loan {}: {}",
                        loanDTO.id(), e.getMessage()))
                .subscribe();
    }

    @RabbitListener(queues = "${rabbitmq.queue.loan-disbursement}")
    public void handleLoanDisbursement(LoanDTO loanDTO) {
        log.info("Received loan disbursement request for ID: {}", loanDTO.id());
        loanService.disburseLoan(loanDTO)
                .doOnSuccess(loan -> log.info("Successfully disbursed loan: {}", loanDTO.id()))
                .doOnError(e -> log.error("Error disbursing loan {}: {}",
                        loanDTO.id(), e.getMessage()))
                .subscribe();
    }
}
