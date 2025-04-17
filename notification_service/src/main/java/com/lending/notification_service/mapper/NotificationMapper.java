package com.lending.notification_service.mapper;

import com.lending.notification_service.dto.NotificationDTO;
import com.lending.notification_service.model.Notification;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationDTO toDto(Notification notification);

    @InheritInverseConfiguration
    Notification toEntity(NotificationDTO notificationDTO);

}
