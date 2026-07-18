package com.acd.researchrepo.service;

import com.acd.researchrepo.dto.external.notifications.NotificationDto;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseEmitterService {

  // one emitter per user — second tab replaces first, use per-connection list if needed
  private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

  /**
   * Creates and registers an SSE emitter for a user.
   *
   * @param userId the ID of the user associated with the emitter
   * @return the registered SSE emitter
   */
  public SseEmitter addEmitter(Integer userId) {
    SseEmitter emitter = new SseEmitter(0L);
    emitters.put(userId, emitter);
    emitter.onCompletion(() -> emitters.remove(userId));
    emitter.onTimeout(() -> emitters.remove(userId));
    emitter.onError(e -> emitters.remove(userId));
    return emitter;
  }

  /**
   * Removes the server-sent events emitter associated with a user.
   *
   * @param userId the ID of the user whose emitter should be removed
   */
  public void removeEmitter(Integer userId) {
    emitters.remove(userId);
  }

  /**
   * Sends a notification event to the user's active SSE connection.
   *
   * @param userId the identifier of the recipient user
   * @param dto the notification data to send
   */
  public void sendToUser(Integer userId, NotificationDto dto) {
    SseEmitter emitter = emitters.get(userId);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name("notification").data(dto));
      } catch (IOException e) {
        emitters.remove(userId);
      }
    }
  }
}
