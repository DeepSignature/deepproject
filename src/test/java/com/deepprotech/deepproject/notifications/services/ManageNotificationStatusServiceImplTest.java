package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageNotificationStatusServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks ManageNotificationStatusServiceImpl service;

    @Test
    void marksNotificationRead() {
        service.handle(new MarkNotificationReadCommand(1L));
        verify(jdbc).update("UPDATE notifications SET status = 'READ', updated_at = CURRENT_TIMESTAMP WHERE id = ?", 1L);
    }

    @Test
    void marksAllNotificationsRead() {
        service.handle(new MarkAllNotificationsReadCommand(1L));
        verify(jdbc).update("UPDATE notifications SET status = 'READ', updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status = 'UNREAD'", 1L);
    }
}