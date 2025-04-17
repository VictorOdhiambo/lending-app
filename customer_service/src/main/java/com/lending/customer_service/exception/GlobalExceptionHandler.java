package com.lending.customer_service.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString();
            // Extract the field name without the method name prefix (if any)
            if (field.contains(".")) {
                field = field.substring(field.lastIndexOf('.') + 1);
            }
            String message = violation.getMessage();
            errors.put(field, message);
        }

        return Mono.just(ResponseEntity.badRequest().body(errors));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleBindException(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return Mono.just(ResponseEntity.badRequest().body(errors));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}