package com.acd.researchrepo.mapper;

import com.acd.researchrepo.dto.external.notifications.NotificationDto;
import com.acd.researchrepo.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

  /**
   * Converts a notification to its data transfer object representation.
   *
   * @param notification the notification to convert
   * @return the corresponding notification DTO, or {@code null} if the notification is {@code null}
   */
  public NotificationDto toDto(Notification notification) {
    if (notification == null) return null;

    return NotificationDto.builder()
        .notificationId(notification.getNotificationId())
        .message(notification.getMessage())
        .type(notification.getType())
        .relatedRequestId(notification.getRelatedRequestId())
        .isRead(notification.getIsRead())
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
