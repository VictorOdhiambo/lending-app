package com.lending.notification_service.service;

import com.lending.notification_service.dto.NotificationDTO;
import com.lending.notification_service.mapper.NotificationMapper;
import com.lending.notification_service.model.Notification;
import com.lending.notification_service.repository.INotificationRepository;
import com.lending.notification_service.service.contract.INotificationService;
import com.lending.notification_service.shared.CustomerCreatedEvent;
import com.lending.notification_service.shared.LoanStatusChangedEvent;
import com.lending.notification_service.shared.NotificationStatus;
import com.lending.notification_service.shared.NotificationType;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

public class NotificationService implements INotificationService {
    private final NotificationMapper notificationMapper;
    private final INotificationRepository notificationRepository;

    public NotificationService(INotificationRepository notificationRepository, NotificationMapper notificationMapper){
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }


    @Override
    public Mono<NotificationDTO> addNotification(NotificationDTO notificationDTO) {
        Notification notification = notificationMapper.toEntity(notificationDTO);
        return notificationRepository.save(notification)
                .map(notificationMapper::toDto);
    }

    @Override
    public Mono<NotificationDTO> editNotification(UUID notificationId, NotificationDTO notificationDTO) {
        return notificationRepository.findById(notificationId)
                .switchIfEmpty(Mono.error(new RuntimeException("Error retrieving notification")))
                .flatMap(notification -> {
                    notification.setStatus(notificationDTO.status());
                    return notificationRepository.save(notification).map(notificationMapper::toDto);
                });
    }

    @Override
    public Flux<NotificationDTO> findPendingNotifications() {
        return notificationRepository.findByStatus(NotificationStatus.PENDING.name())
                .map(notificationMapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("No pending notifications found")));
    }

    @Override
    public Mono<NotificationDTO> findNotificationById(UUID id) {
        return notificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Error retrieving notifications details")))
                .map(notificationMapper::toDto);
    }

    @Override
    public Flux<NotificationDTO> findNotificationByCustomerId(UUID customerId) {
        return notificationRepository.findByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new RuntimeException("Error retrieving notifications details")))
                .map(notificationMapper::toDto);
    }

    @Bean
    public Consumer<CustomerCreatedEvent> processCustomerCreated() {
        return event -> {
            System.out.println("Received customer created event: " + event.getId());

            if (event.getId() == null){
                return;
            }

            NotificationDTO notificationDto = new NotificationDTO(
                    null,
                    UUID.fromString(event.getId()),
                    null,
                    NotificationType.CUSTOMER_CREATED.name(),
                    "Welcome to our service, " + event.getName() + "! We're glad to have you on board.",
                    NotificationStatus.PENDING.name(),
                    LocalDateTime.now()
            );

            notificationRepository.save(notificationMapper.toEntity(notificationDto))
                    .doOnSuccess(saved -> System.out.println(NotificationType.CUSTOMER_CREATED.name() + " notification sent to customer: " + event.getId()))
                    .subscribe();
        };
    }

    @Bean
    public Consumer<LoanStatusChangedEvent> processLoanStatusChanged() {
        return event -> {
            System.out.println("Received loan status changed event: " + event.getLoanId() +
                    " from " + event.getPreviousStatus() + " to " + event.getNewStatus());

            String message = "";
            String type = "";

            if (event.getLoanId() == null){
                return;
            }

            message = switch (event.getNewStatus()) {
                case "OPEN" -> {
                    type = "LOAN_APPROVED";
                    yield "Congratulations! Your loan application (ID: " + event.getLoanId() + ") has been approved.";
                }
                case "CANCELLED" -> {
                    type = "LOAN_CANCELLED";
                    yield "We're sorry to inform you that your loan application (ID: " + event.getLoanId() + ") has been declined.";
                }
                case "DUE" -> {
                    type = "LOAN_DUE";
                    yield "Reminder: Your loan payment for loan (ID: " + event.getLoanId() + ") is due soon. Please make your payment to avoid late fees.";
                }
                case "OVERDUE" -> {
                    type = "LOAN_OVERDUE";
                    yield "Important: Your loan payment for loan (ID: " + event.getLoanId() + ") is overdue. Please make your payment as soon as possible to avoid additional penalties.";
                }
                default -> {
                    type = "LOAN_STATUS_CHANGED";
                    yield "Your loan (ID: " + event.getLoanId() + ") status has been updated to " + event.getNewStatus() + ".";
                }
            };

            NotificationDTO notificationDto = new NotificationDTO(
                    null,
                    null,
                    UUID.fromString(event.getLoanId()),
                    type,
                    message,
                    NotificationStatus.PENDING.name(),
                    LocalDateTime.now()
            );
            notificationRepository.save(notificationMapper.toEntity(notificationDto))
                    .doOnSuccess(saved -> System.out.println("Notification sent to customer: " + event.getCustomerId() + " for loan: " + event.getLoanId()))
                    .subscribe();
        };
    }
}
