package com.lending.notification_service.repository;

import com.lending.notification_service.model.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface INotificationRepository extends ReactiveCrudRepository<Notification, UUID> {
    Flux<Notification> findByStatus(String status);
    Flux<Notification> findByCustomerId(UUID customerId);
}
