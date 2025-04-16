package com.lending.scheduler_service.service;

import com.lending.scheduler_service.dto.LoanDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanSweepService {
    private final LoanServiceClient loanServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    private final MeterRegistry meterRegistry;

    @Scheduled(cron = "${app.scheduler.overdue-loan-check.cron:0 0 1 * * ?}")
    public void checkOverdueLoans() {
        log.info("Starting scheduled job: checkOverdueLoans");
        long startTime = System.currentTimeMillis();

        loanServiceClient.getOverdueLoans()
                .doOnNext(loan -> {
                    log.info("Processing overdue loan: {}", loan.getId());
                    meterRegistry.counter("scheduler.overdue.processed").increment();
                })
                .flatMap(this::processOverdueLoan)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Completed scheduled job: checkOverdueLoans in {}ms", duration);
                    meterRegistry.timer("scheduler.overdue.duration").record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                })
                .subscribe();
    }

    @Scheduled(cron = "${app.scheduler.due-date-reminder.cron:0 0 9 * * ?}")
    public void sendPaymentReminders() {
        log.info("Starting scheduled job: sendPaymentReminders");
        long startTime = System.currentTimeMillis();

        LocalDate dueDate = LocalDate.now().plusDays(3);

        loanServiceClient.getLoansDueOn(dueDate)
                .doOnNext(loan -> {
                    log.info("Processing payment reminder for loan: {}", loan.getId());
                    meterRegistry.counter("scheduler.reminder.processed").increment();
                })
                .flatMap(this::sendPaymentReminder)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Completed scheduled job: sendPaymentReminders in {}ms", duration);
                    meterRegistry.timer("scheduler.reminder.duration").record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                })
                .subscribe();
    }

    private Mono<Void> processOverdueLoan(LoanDTO loan) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("loanId", loan.getId());
        notificationData.put("customerName", loan.getCustomerName());
        notificationData.put("amountDue", loan.getAmountDue());
        notificationData.put("daysOverdue", LocalDate.now().compareTo(loan.getDueDate()));

        return notificationServiceClient.sendOverdueNotification(loan.getCustomerId(), notificationData)
                .doOnSuccess(v -> log.info("Sent overdue notification for loan: {}", loan.getId()))
                .doOnError(e -> {
                    log.error("Failed to send overdue notification for loan: {}", loan.getId(), e);
                    meterRegistry.counter("scheduler.overdue.notification.error").increment();
                });
    }

    private Mono<Void> sendPaymentReminder(LoanDTO loan) {
        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("loanId", loan.getId());
        reminderData.put("customerName", loan.getCustomerName());
        reminderData.put("amountDue", loan.getAmountDue());
        reminderData.put("dueDate", loan.getDueDate().toString());

        return notificationServiceClient.sendPaymentReminder(loan.getCustomerId(), reminderData)
                .doOnSuccess(v -> log.info("Sent payment reminder for loan: {}", loan.getId()))
                .doOnError(e -> {
                    log.error("Failed to send payment reminder for loan: {}", loan.getId(), e);
                    meterRegistry.counter("scheduler.reminder.notification.error").increment();
                });
    }
}
