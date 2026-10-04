package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.notifications.api.ManageNotificationStatusService;
import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;
import com.deepprotech.deepproject.notifications.constants.NotificationStatus;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageNotificationStatusServiceImpl implements ManageNotificationStatusService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void handle(MarkNotificationReadCommand command) {
        notificationRepository.findById(command.notificationId()).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.READ.name());
            notificationRepository.save(notification);
        });
        log.info("notification_marked_read id={}", command.notificationId());
    }

    @Override
    @Transactional
    public void handle(MarkAllNotificationsReadCommand command) {
        notificationRepository.markAllAsReadByUserId(command.userId());
        log.info("all_notifications_marked_read userId={}", command.userId());
    }
}
