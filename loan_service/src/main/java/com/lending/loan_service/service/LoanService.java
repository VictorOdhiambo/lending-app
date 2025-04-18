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
import com.lending.loan_service.shared.LoanStatus;
import com.lending.loan_service.shared.LoanStatusChangedEvent;
import com.lending.loan_service.shared.PaymentFrequency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class LoanService implements ILoanService {
    private final LoanMapper loanMapper;
    private final RepaymentRequestMapper repaymentRequestMapper;
    private final ILoanRepository loanRepository;
    private final IRepaymentRequestRepository repaymentRequestRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.loan}")
    private String loanExchange;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routing-key.process-loan}")
    private String processLoanRoutingKey;

    @Value("${rabbitmq.routing-key.approve-loan}")
    private String approveLoanRoutingKey;

    @Value("${rabbitmq.routing-key.disburse-loan}")
    private String disburseLoanRoutingKey;

    @Value("${rabbitmq.routing-key.repay-loan}")
    private String loanRepaymentKey;

    @Value("${rabbitmq.routing-key.loan-status}")
    private String loanStatusKey;

    public LoanService(ILoanRepository loanRepository, IRepaymentRequestRepository repaymentRequestRepository,
                       RepaymentRequestMapper repaymentRequestMapper, LoanMapper loanMapper, RabbitTemplate rabbitTemplate){
        this.loanRepository = loanRepository;
        this.repaymentRequestRepository = repaymentRequestRepository;
        this.repaymentRequestMapper = repaymentRequestMapper;
        this.loanMapper = loanMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Mono<LoanDTO> addLoan(LoanDTO loanDTO) {
        Loan loan = loanMapper.toEntity(loanDTO);
        return loanRepository.save(loan)
                .map(loanMapper::toDto)
                .switchIfEmpty(Mono.error(new LoanException("Error when creating loan")))
                .doOnSuccess(savedLoan -> {
                    log.info("Loan application created with ID: {}", savedLoan.id());
                    log.info("Queueing loan for processing: {}", savedLoan.id());
                    rabbitTemplate.convertAndSend(loanExchange, processLoanRoutingKey, savedLoan.id());
                    // record event
                    rabbitTemplate.convertAndSend(notificationExchange, loanStatusKey,
                            new LoanStatusChangedEvent(savedLoan.id().toString(), savedLoan.customerId().toString(), "", LoanStatus.OPEN.name()));
                });
    }

    @Override
    public Mono<LoanDTO> processLoan(String loanId) {
        log.info("Processing loan application: {}", loanId);

        return loanRepository.findById(UUID.fromString(loanId))
                .flatMap(loan -> {
                    if (!Objects.equals(loan.getLoanStatus(), LoanStatus.OPEN.name())) {
                        return Mono.error(new IllegalStateException("Loan is not in OPEN state"));
                    }
                    rabbitTemplate.convertAndSend(loanExchange, approveLoanRoutingKey, loan);
                    return Mono.just(loanMapper.toDto(loan));
                })
                .doOnError(e -> log.error("Error processing loan {}: {}", loanId, e.getMessage()));
    }


    @Override
    public Mono<RepaymentRequestDTO> addRepayment(RepaymentRequestDTO repaymentRequestDTO) {
        RepaymentRequest repaymentRequest = repaymentRequestMapper.toEntity(repaymentRequestDTO);

        return repaymentRequestRepository.save(repaymentRequest)
                .flatMap(request ->
                        loanRepository.findById(request.getLoanId())
                                .flatMap(loan -> {
                                    loan.setRepaidAmount(loan.getRepaidAmount() + request.getAmount());
                                    return loanRepository.save(loan)
                                            .thenReturn(request);
                                })
                )
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
    public Mono<LoanDTO> approveLoan(LoanDTO loanDTO) {
        log.info("Applying approval decision for loan: {}", loanDTO.id());

        return loanRepository.findById(loanDTO.id())
                .flatMap(loan -> {
                    loan.setStartDate(LocalDateTime.now());

                    LocalDateTime endDate = switch (loan.getPaymentFrequency()) {
                        case "DAILY" -> LocalDateTime.now().plusDays(PaymentFrequency.DAILY.getValue());
                        case "WEEKLY" -> LocalDateTime.now().plusDays(PaymentFrequency.WEEKLY.getValue());
                        case "BI_WEEKLY" -> LocalDateTime.now().plusDays(PaymentFrequency.BI_WEEKLY.getValue());
                        case "MONTHLY" -> LocalDateTime.now().plusMonths(PaymentFrequency.MONTHLY.getValue());
                        case "QUARTERLY" -> LocalDateTime.now().plusMonths(PaymentFrequency.QUARTERLY.getValue());
                        case "SEMI_ANNUALLY" -> LocalDateTime.now().plusMonths(PaymentFrequency.SEMI_ANNUALLY.getValue());
                        case "YEARLY" -> LocalDateTime.now().plusMonths(PaymentFrequency.ANNUALLY.getValue());
                        default -> {
                            log.warn("Unknown payment frequency '{}', defaulting end date to start date", loan.getPaymentFrequency());
                            yield loan.getStartDate();
                        }
                    };

                    loan.setEndDate(endDate);

                    rabbitTemplate.convertAndSend(loanExchange, disburseLoanRoutingKey, loanDTO);

                    return loanRepository.save(loan)
                            .map(loanMapper::toDto);
                })
                .doOnError(e -> log.error("Error processing approval for loan {}: {}", loanDTO.id(), e.getMessage()));
    }


    @Override
    public Mono<LoanDTO> disburseLoan(LoanDTO loanDTO) {
        log.info("Disbursing loan: {}", loanDTO.id());

        return loanRepository.findById(loanDTO.id())
                .flatMap(loan -> {
                    if (!Objects.equals(loan.getLoanStatus(), LoanStatus.OPEN.name())) {
                        return Mono.error(new IllegalStateException("Loan is not in OPEN state"));
                    }

                    loan.setDisbursementDate(LocalDateTime.now());
                    loan.setDisbursementAmount(loan.getAppliedAmount());

                    return loanRepository.save(loan)
                            .doOnSuccess(savedLoan -> {
                                rabbitTemplate.convertAndSend(notificationExchange, loanStatusKey,
                                        new LoanStatusChangedEvent(savedLoan.getId().toString(), savedLoan.getCustomerId().toString(), LoanStatus.OPEN.name(), LoanStatus.CANCELLED.name()));
                            })
                            .map(loanMapper::toDto);
                })
                .doOnError(e -> log.error("Error disbursing loan {}: {}",
                        loanDTO.id(), e.getMessage()));
    }

    @Override
    public Mono<LoanDTO> repayLoan(RepaymentRequestDTO repaymentRequestDTO){
        log.info("Loan repayment: {}", repaymentRequestDTO.loanId());

        return loanRepository.findById(repaymentRequestDTO.loanId())
                .flatMap(loan -> {
                    loan.setRepaidAmount(loan.getRepaidAmount() + repaymentRequestDTO.amount());
                    return loanRepository.save(loan)
                            .flatMap(persisted -> repaymentRequestRepository.findById(repaymentRequestDTO.id())
                                    .flatMap(repayment -> {
                                        repayment.setStatus("COMPLETED");
                                        return repaymentRequestRepository.save(repayment);
                                    }).thenReturn(loan))
                            .map(loanMapper::toDto);
                })
                .doOnError(e -> log.error("Error repaying loan {}: {}",
                        repaymentRequestDTO.loanId(), e.getMessage()));
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
