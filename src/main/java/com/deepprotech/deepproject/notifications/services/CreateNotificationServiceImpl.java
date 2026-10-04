package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.api.CreateNotificationService;
import com.deepprotech.deepproject.notifications.commands.CreateNotificationCommand;
import com.deepprotech.deepproject.notifications.constants.NotificationStatus;
import com.deepprotech.deepproject.notifications.constants.NotificationType;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateNotificationServiceImpl implements CreateNotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Notification handle(CreateNotificationCommand command) {
        Notification n = Notification.builder()
                .userId(command.userId())
                .title(command.title())
                .message(command.message())
                .notificationType(command.notificationType() != null ? command.notificationType() : NotificationType.INFO.name())
                .status(NotificationStatus.UNREAD.name())
                .entityType(command.entityType())
                .entityId(command.entityId())
                .build();

        n = notificationRepository.save(n);
        log.info("notification_created id={} userId={}", n.getId(), command.userId());
        return n;
    }
}
