package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.notifications.api.ManageNotificationStatusService;
import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageNotificationStatusServiceImpl implements ManageNotificationStatusService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(MarkNotificationReadCommand command) {
        jdbc.update("UPDATE notifications SET status = 'READ', updated_at = CURRENT_TIMESTAMP WHERE id = ?", command.notificationId());
        log.info("notification_marked_read id={}", command.notificationId());
    }

    @Override
    @Transactional
    public void handle(MarkAllNotificationsReadCommand command) {
        jdbc.update("UPDATE notifications SET status = 'READ', updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status = 'UNREAD'", command.userId());
        log.info("all_notifications_marked_read userId={}", command.userId());
    }
}
