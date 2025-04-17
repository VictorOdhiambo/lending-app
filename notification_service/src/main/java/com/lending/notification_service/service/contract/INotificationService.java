package com.lending.notification_service.service.contract;

import com.lending.notification_service.dto.NotificationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface INotificationService {
    Mono<NotificationDTO> addNotification(NotificationDTO notificationDTO);
    Mono<NotificationDTO> editNotification(UUID notificationId, NotificationDTO notificationDTO);
    Flux<NotificationDTO> findPendingNotifications();
    Mono<NotificationDTO> findNotificationById(UUID id);
    Flux<NotificationDTO> findNotificationByCustomerId(UUID customerId);
}
