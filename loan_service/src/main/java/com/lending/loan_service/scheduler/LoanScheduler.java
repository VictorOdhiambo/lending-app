package com.lending.loan_service.scheduler;


import com.lending.loan_service.mapper.LoanMapper;
import com.lending.loan_service.repository.ILoanRepository;
import com.lending.loan_service.service.contract.ILoanService;
import com.lending.loan_service.shared.LoanStatus;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanScheduler {

    private final ILoanRepository loanRepository;
    private final ILoanService loanService;
    private final MeterRegistry meterRegistry;
    private final LoanMapper loanMapper;

    @Value("${loan.application.expiry-days:7}")
    private int applicationExpiryDays;

    @Scheduled(cron = "${scheduler.overdue-check.cron:0 0 1 * * ?}")
    public void checkOverdueLoans() {
        log.info("Starting scheduled job: Check for overdue loans");
        long startTime = System.currentTimeMillis();

        loanService.findOverdueLoans()
                .doOnNext(loan -> {
                    log.info("Processing overdue loan: {}", loan.id());
                    meterRegistry.counter("scheduler.overdue.processed").increment();
                })
                .flatMap(loan ->
                        loanRepository.findById(loan.id())
                                .flatMap(persisted -> {
                                    persisted.setLoanStatus(LoanStatus.OVERDUE.name());
                                    return loanRepository.save(persisted).map(loanMapper::toDto);
                                })
                )
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Completed scheduled job: Check for overdue loans in {}ms", duration);
                    meterRegistry.timer("scheduler.overdue.duration")
                            .record(duration, TimeUnit.MILLISECONDS);
                })
                .doOnError(e -> log.error("Error while processing overdue loans", e))
                .subscribe();
    }


    @Scheduled(cron = "${scheduler.stale-application-check.cron:0 0 2 * * ?}")
    public void cancelStaleApplications() {
        log.info("Starting scheduled job: Cancel stale loan applications");
        long startTime = System.currentTimeMillis();

        LocalDate cutoffDate = LocalDate.now().minusDays(applicationExpiryDays);

        loanRepository.findAll()
                .doOnNext(loan -> {
                    log.info("Processing stale loan application: {}", loan.getId());
                    meterRegistry.counter("scheduler.stale-application.processed").increment();
                })
                .filter(loan -> loan.getDisbursementDate() == null || loan.getStartDate().isBefore(cutoffDate.atStartOfDay()))
                .flatMap(loan ->
                        loanRepository.findById(loan.getId())
                                .flatMap(persisted -> {
                                    persisted.setLoanStatus(LoanStatus.CANCELLED.name());
                                    return loanRepository.save(persisted).map(loanMapper::toDto);
                                })
                )
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Completed scheduled job: Cancel stale loan applications in {}ms", duration);
                    meterRegistry.timer("scheduler.stale-application.duration")
                            .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                })
                .subscribe();
    }
}
