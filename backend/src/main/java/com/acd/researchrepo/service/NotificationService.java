package com.acd.researchrepo.service;

import com.acd.researchrepo.dto.external.notifications.NotificationDto;
import com.acd.researchrepo.dto.external.papers.PaginatedResponse;
import com.acd.researchrepo.mapper.NotificationMapper;
import com.acd.researchrepo.model.Notification;
import com.acd.researchrepo.repository.NotificationRepository;
import com.acd.researchrepo.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final SseEmitterService sseEmitterService;
  private final UserRepository userRepository;

  public NotificationService(
      NotificationRepository notificationRepository,
      NotificationMapper notificationMapper,
      SseEmitterService sseEmitterService,
      UserRepository userRepository) {
    this.notificationRepository = notificationRepository;
    this.notificationMapper = notificationMapper;
    this.sseEmitterService = sseEmitterService;
    this.userRepository = userRepository;
  }

  /**
   * Creates a notification, persists it, and sends it to the specified user.
   *
   * @param userId           the ID of the notification recipient
   * @param message          the notification message
   * @param type             the notification type
   * @param relatedRequestId the ID of the related request
   * @return the created notification
   */
  @Transactional
  public NotificationDto createAndSend(
      Integer userId, String message, String type, Integer relatedRequestId) {

    Notification notification = new Notification();
    notification.setUser(userRepository.getReferenceById(userId));
    notification.setMessage(message);
    notification.setType(type);
    notification.setRelatedRequestId(relatedRequestId);
    notification.setIsRead(false);

    notification = notificationRepository.save(notification);
    NotificationDto dto = notificationMapper.toDto(notification);

    sseEmitterService.sendToUser(userId, dto);

    return dto;
  }

  /**
   * Retrieves a user's notifications in descending creation order.
   *
   * @param userId   the ID of the user whose notifications are retrieved
   * @param pageable pagination and page-size settings
   * @return a paginated response containing the user's notification DTOs
   */
  public PaginatedResponse<NotificationDto> getNotifications(Integer userId, Pageable pageable) {
    return PaginatedResponse.fromPage(
        notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId, pageable),
        notificationMapper::toDto);
  }

  /**
   * Counts the unread notifications for a user.
   *
   * @param userId the ID of the user
   * @return the number of unread notifications
   */
  public long getUnreadCount(Integer userId) {
    return notificationRepository.countByUserUserIdAndIsReadFalse(userId);
  }

  /**
   * Marks all notifications belonging to a user as read.
   *
   * @param userId the ID of the user whose notifications should be marked as read
   */
  @Transactional
  public void markAllRead(Integer userId) {
    notificationRepository.markAllReadByUserId(userId);
  }
}
