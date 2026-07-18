package com.acd.researchrepo.repository;

import com.acd.researchrepo.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

  /**
 * Retrieves a user's notifications in reverse chronological order.
 *
 * @return a page of notifications ordered by creation time descending
 */
Page<Notification> findByUserUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

  /**
 * Counts unread notifications belonging to a user.
 *
 * @param userId the user's identifier
 * @return the number of unread notifications for the user
 */
long countByUserUserIdAndIsReadFalse(Integer userId);

  /**
   * Marks all notifications for a user as read.
   *
   * @param userId the identifier of the user whose notifications are updated
   */
  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.userId = :userId")
  void markAllReadByUserId(@Param("userId") Integer userId);
}
