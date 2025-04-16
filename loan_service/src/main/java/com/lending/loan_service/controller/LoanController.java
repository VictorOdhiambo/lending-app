package com.lending.loan_service.controller;

import com.lending.loan_service.dto.LoanDTO;
import com.lending.loan_service.dto.RepaymentRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @PostMapping
    public ResponseEntity<?> createLoan(@RequestBody LoanDTO loanDTO){
        return ResponseEntity.ok("Loan Created successfully");
    }

    @PostMapping("/repay/{id}")
    public ResponseEntity<?> repayLoan(@RequestBody RepaymentRequestDTO repaymentRequestDTO){
        return ResponseEntity.ok("Repayment queued successfully");
    }

    @GetMapping
    public ResponseEntity<?> getLoan(){
        return ResponseEntity.ok("Loan Service");
    }
}
