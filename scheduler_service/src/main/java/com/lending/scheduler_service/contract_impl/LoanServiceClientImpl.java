package com.lending.scheduler_service.contract_impl;

import com.lending.scheduler_service.contracts.ILoanServiceClient;
import com.lending.scheduler_service.dto.LoanDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanServiceClientImpl implements ILoanServiceClient {

    private final WebClient webClient;

    @Value("${app.loan-service.base-url}")
    private String loanServiceBaseUrl;

    @Value("${app.loan-service.timeout-seconds:10}")
    private int timeoutSeconds;

    @Override
    public Flux<LoanDTO> getOverdueLoans() {
        return webClient.get()
                .uri(loanServiceBaseUrl + "/loan/overdue")
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new LoanServiceException(
                                        "Error fetching overdue loans: " + error,
                                        response.statusCode().value()
                                )))
                )
                .bodyToFlux(LoanDTO.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnSubscribe(s -> log.debug("Fetching overdue loans"))
                .doOnComplete(() -> log.debug("Completed fetching overdue loans"))
                .doOnError(e -> log.error("Error fetching overdue loans", e));
    }

    @Override
    public Flux<LoanDTO> getLoansDueOn(LocalDate dueDate) {
        String formattedDate = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(loanServiceBaseUrl + "/loan/due")
                        .queryParam("date", formattedDate)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new LoanServiceException(
                                        "Error fetching loans due on " + formattedDate + ": " + error,
                                        response.statusCode().value()
                                )))
                )
                .bodyToFlux(LoanDTO.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnSubscribe(s -> log.debug("Fetching loans due on {}", formattedDate))
                .doOnComplete(() -> log.debug("Completed fetching loans due on {}", formattedDate))
                .doOnError(e -> log.error("Error fetching loans due on {}", formattedDate, e));
    }

    @Override
    public Flux<LoanDTO> getLoansByCustomerId(String customerId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(loanServiceBaseUrl + "/loan/customer/{customerId}")
                        .build(customerId))
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new LoanServiceException(
                                        "Error fetching loans for customer " + customerId + ": " + error,
                                        response.statusCode().value()
                                )))
                )
                .bodyToFlux(LoanDTO.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnSubscribe(s -> log.debug("Fetching loans for customer {}", customerId))
                .doOnComplete(() -> log.debug("Completed fetching loans for customer {}", customerId))
                .doOnError(e -> log.error("Error fetching loans for customer {}", customerId, e));
    }

    @Override
    public Flux<LoanDTO> getLoansAtRiskOfDefault() {
        return webClient.get()
                .uri(loanServiceBaseUrl + "/loan/at-risk")
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new LoanServiceException(
                                        "Error fetching loans at risk of default: " + error,
                                        response.statusCode().value()
                                )))
                )
                .bodyToFlux(LoanDTO.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnSubscribe(s -> log.debug("Fetching loans at risk of default"))
                .doOnComplete(() -> log.debug("Completed fetching loans at risk of default"))
                .doOnError(e -> log.error("Error fetching loans at risk of default", e));
    }

    @Override
    public Flux<LoanDTO> updateLoanStatus(String loanId, String status) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(loanServiceBaseUrl + "/loan/{loanId}/status")
                        .build(loanId))
                .bodyValue(status)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new LoanServiceException(
                                        "Error updating status for loan " + loanId + ": " + error,
                                        response.statusCode().value()
                                )))
                )
                .bodyToFlux(LoanDTO.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnSubscribe(s -> log.debug("Updating status for loan {}", loanId))
                .doOnComplete(() -> log.debug("Completed updating status for loan {}", loanId))
                .doOnError(e -> log.error("Error updating status for loan {}", loanId, e));
    }
}
