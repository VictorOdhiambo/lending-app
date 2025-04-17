package com.lending.loan_service.controller;

import com.lending.loan_service.dto.LoanDTO;
import com.lending.loan_service.dto.RepaymentRequestDTO;
import com.lending.loan_service.service.contract.ILoanService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private Validator validator;

    private final ILoanService loanService;

    public LoanController(ILoanService loanService){
        this.loanService = loanService;
    }

    @PostMapping("/apply")
    public Mono<ResponseEntity<LoanDTO>> createLoan(@RequestBody Mono<LoanDTO> loanDTO){
        return loanDTO.doOnNext(this::validate)
                .flatMap(loanService::addLoan)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<LoanDTO>> editLoan(@PathVariable("id") String id, @RequestBody Mono<LoanDTO> loanDTO){
        return loanDTO.doOnNext(this::validate)
                .flatMap(loan -> loanService.editLoan(id, loan))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/repay-loan")
    public Mono<ResponseEntity<RepaymentRequestDTO>> repayLoan(@RequestBody Mono<RepaymentRequestDTO> repaymentRequestDTO){
        return repaymentRequestDTO.doOnNext(this::validate)
                .flatMap(loanService::addRepayment)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/due-loans")
    public ResponseEntity<Flux<LoanDTO>> getDueLoans(){
        return ResponseEntity.ok(loanService.findDueLoans());
    }

    @GetMapping("/overdue-loans")
    public ResponseEntity<Flux<LoanDTO>> getOverdueLoans(){
        return ResponseEntity.ok(loanService.findOverdueLoans());
    }

    @GetMapping("/all-by-customer-id/{id}")
    public ResponseEntity<Flux<LoanDTO>> findLoansByCustomerId(@PathVariable("id") String id){
        return ResponseEntity.ok(loanService.findLoansByCustomerId(UUID.fromString(id)));
    }

    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
