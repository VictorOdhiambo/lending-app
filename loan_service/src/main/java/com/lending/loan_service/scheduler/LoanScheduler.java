package com.lending.loan_service.scheduler;


import com.lending.loan_service.mapper.LoanMapper;
import com.lending.loan_service.repository.ILoanRepository;
import com.lending.loan_service.service.contract.ILoanService;
import com.lending.loan_service.shared.LoanStatus;
import com.lending.loan_service.shared.LoanStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@Slf4j
public class LoanScheduler {

    private final ILoanRepository loanRepository;
    private final ILoanService loanService;
    private final LoanMapper loanMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${loan.application.expiry-days:7}")
    private int applicationExpiryDays;

    @Value("${rabbitmq.routing-key.loan-status}")
    private String loanStatusKey;

    public LoanScheduler(ILoanRepository loanRepository, ILoanService loanService,
                         LoanMapper loanMapper, RabbitTemplate rabbitTemplate){
        this.loanRepository = loanRepository;
        this.loanService = loanService;
        this.loanMapper = loanMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(cron = "${scheduler.overdue-check.cron:0 0/2 * * * ?}")
    public void checkOverdueLoans() {
        log.info("Starting scheduled job: Check for overdue loans");
        long startTime = System.currentTimeMillis();

        loanService.findOverdueLoans()
                .doOnNext(loan -> {
                    log.info("Processing overdue loan: {}", loan.id());
                })
                .flatMap(loan ->
                        loanRepository.findById(loan.id())
                                .flatMap(persisted -> {
                                    persisted.setLoanStatus(LoanStatus.OVERDUE.name());
                                    return loanRepository.save(persisted)
                                            .doOnSuccess(savedLoan -> {
                                                rabbitTemplate.convertAndSend(notificationExchange, loanStatusKey,
                                                        new LoanStatusChangedEvent(savedLoan.getId().toString(), savedLoan.getCustomerId().toString(), LoanStatus.OPEN.name(), LoanStatus.OVERDUE.name()));
                                            })
                                            .map(loanMapper::toDto);
                                })
                )
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Completed scheduled job: Check for overdue loans in {}ms", duration);
                })
                .doOnError(e -> log.error("Error while processing overdue loans", e))
                .subscribe();
    }


    @Scheduled(cron = "${scheduler.stale-application-check.cron:0 0/2 * * * ?}")
    public void cancelStaleApplications() {
        log.info("Starting scheduled job: Cancel stale loan applications");
        long startTime = System.currentTimeMillis();

        LocalDate cutoffDate = LocalDate.now().minusDays(applicationExpiryDays);

        loanRepository.findAll()
                .doOnNext(loan -> {
                    log.info("Processing stale loan application: {}", loan.getId());
                })
                .filter(loan -> loan.getDisbursementDate() == null || loan.getCreatedDate().isBefore(cutoffDate.atStartOfDay()))
                .flatMap(loan ->
                        loanRepository.findById(loan.getId())
                                .flatMap(persisted -> {
                                    persisted.setLoanStatus(LoanStatus.CANCELLED.name());
                                    return loanRepository.save(persisted)
                                            .doOnSuccess(savedLoan -> {
                                                rabbitTemplate.convertAndSend(notificationExchange, loanStatusKey,
                                                        new LoanStatusChangedEvent(savedLoan.getId().toString(), savedLoan.getCustomerId().toString(), LoanStatus.OPEN.name(), LoanStatus.CANCELLED.name()));
                                            })
                                            .map(loanMapper::toDto);
                                })
                )
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Completed scheduled job: Cancel stale loan applications in {}ms", duration);
                })
                .subscribe();
    }
}
