package com.lending.loan_service.exception;

public class RepaymentRequestException extends RuntimeException{
    public RepaymentRequestException(String message){
        super(message);
    }
}
