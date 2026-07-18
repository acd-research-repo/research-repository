package com.acd.researchrepo.controller;

import com.acd.researchrepo.dto.external.notifications.NotificationDto;
import com.acd.researchrepo.dto.external.papers.PaginatedResponse;
import com.acd.researchrepo.security.CustomUserPrincipal;
import com.acd.researchrepo.service.NotificationService;
import com.acd.researchrepo.service.SseEmitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final SseEmitterService sseEmitterService;

  /**
   * Creates a notification controller with the services used by its endpoints.
   *
   * @param notificationService service for retrieving and updating notifications
   * @param sseEmitterService service for managing notification event streams
   */
  public NotificationController(
      NotificationService notificationService, SseEmitterService sseEmitterService) {
    this.notificationService = notificationService;
    this.sseEmitterService = sseEmitterService;
  }

  /**
   * Opens a server-sent event stream for the authenticated user.
   *
   * @param principal the authenticated user's security principal
   * @return the emitter for the user's notification stream
   */
  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamNotifications(
      @AuthenticationPrincipal CustomUserPrincipal principal) {
    log.debug("GET /api/notifications/stream endpoint hit for user {}", principal.getUserId());
    return sseEmitterService.addEmitter(principal.getUserId());
  }

  /**
   * Retrieves a paginated list of notifications for the authenticated user.
   *
   * @param page the zero-based page number
   * @param size the number of notifications per page
   * @param principal the authenticated user's principal
   * @return the user's notifications for the requested page
   */
  @GetMapping
  public ResponseEntity<PaginatedResponse<NotificationDto>> getNotifications(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @AuthenticationPrincipal CustomUserPrincipal principal) {
    log.debug("GET /api/notifications endpoint hit");
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(notificationService.getNotifications(principal.getUserId(), pageable));
  }

  /**
   * Retrieves the number of unread notifications for the authenticated user.
   *
   * @param principal the authenticated user's principal
   * @return the user's unread notification count
   */
  @GetMapping("/unread-count")
  public ResponseEntity<Long> getUnreadCount(
      @AuthenticationPrincipal CustomUserPrincipal principal) {
    log.debug("GET /api/notifications/unread-count endpoint hit");
    long count = notificationService.getUnreadCount(principal.getUserId());
    return ResponseEntity.ok(count);
  }

  /**
   * Marks all notifications for the authenticated user as read.
   *
   * @param principal the authenticated user's principal
   * @return an empty response with HTTP status 204
   */
  @PutMapping("/mark-all-read")
  public ResponseEntity<Void> markAllRead(
      @AuthenticationPrincipal CustomUserPrincipal principal) {
    log.debug("PUT /api/notifications/mark-all-read endpoint hit");
    notificationService.markAllRead(principal.getUserId());
    return ResponseEntity.noContent().build();
  }
}
