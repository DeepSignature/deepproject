package com.deepprotech.deepproject.notifications.repository;

import com.deepprotech.deepproject.core.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC, n.id DESC")
    List<Notification> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT n FROM Notification n
            WHERE n.userId = :userId
              AND (n.createdAt < :createdAt OR (n.createdAt = :createdAt AND n.id < :id))
            ORDER BY n.createdAt DESC, n.id DESC
            """)
    List<Notification> findByUserIdBefore(@Param("userId") UUID userId,
                                          @Param("createdAt") Instant createdAt,
                                          @Param("id") UUID id,
                                          Pageable pageable);

    @Query("""
            SELECT n FROM Notification n
            WHERE n.userId = :userId AND n.status = :status
            ORDER BY n.createdAt DESC, n.id DESC
            """)
    List<Notification> findByUserIdAndStatus(@Param("userId") UUID userId,
                                             @Param("status") String status,
                                             Pageable pageable);

    @Query("""
            SELECT n FROM Notification n
            WHERE n.userId = :userId AND n.status = :status
              AND (n.createdAt < :createdAt OR (n.createdAt = :createdAt AND n.id < :id))
            ORDER BY n.createdAt DESC, n.id DESC
            """)
    List<Notification> findByUserIdAndStatusBefore(@Param("userId") UUID userId,
                                                   @Param("status") String status,
                                                   @Param("createdAt") Instant createdAt,
                                                   @Param("id") UUID id,
                                                   Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ' WHERE n.userId = :userId AND n.status = 'UNREAD'")
    void markAllAsReadByUserId(@Param("userId") UUID userId);
}
