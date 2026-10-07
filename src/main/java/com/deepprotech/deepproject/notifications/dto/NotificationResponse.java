package com.deepprotech.deepproject.notifications.dto;

import com.deepprotech.deepproject.core.Notification;

import java.util.UUID;

public record NotificationResponse(UUID id, UUID userId, String title, String message,
                                   String notificationType, String status, String entityType, UUID entityId) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getUserId(), n.getTitle(), n.getMessage(),
                n.getNotificationType(), n.getStatus(), n.getEntityType(), n.getEntityId());
    }
}
