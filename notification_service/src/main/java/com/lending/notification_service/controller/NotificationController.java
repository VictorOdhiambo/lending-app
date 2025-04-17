package com.lending.notification_service.controller;

import com.lending.notification_service.dto.NotificationDTO;
import com.lending.notification_service.service.contract.INotificationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/notification/create")
public class NotificationController {

    @Autowired
    private Validator validator;

    private final INotificationService notificationService;


    public NotificationController(INotificationService notificationService){
        this.notificationService = notificationService;
    }

    @PostMapping
    public Mono<ResponseEntity<NotificationDTO>> createNotification(@RequestBody Mono<NotificationDTO> dtoMono){
        return dtoMono.doOnNext(this::validate)
                .flatMap(notificationService::addNotification)
                .map(ResponseEntity::ok);
    }

    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
