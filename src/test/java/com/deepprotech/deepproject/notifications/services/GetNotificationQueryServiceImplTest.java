package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNotificationQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetNotificationQueryServiceImpl service;

    private final Notification notif = Notification.builder().id(1L).userId(1L).title("T").message("M").notificationType("INFO").status("UNREAD").build();

    @Test
    void listUserNotificationsReturnsNotifications() {
        when(jdbc.query(eq("SELECT * FROM notifications WHERE user_id = ? ORDER BY id DESC"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(notif));
        assertThat(service.handle(new ListUserNotificationsQuery(1L))).hasSize(1);
    }

    @Test
    void listUnreadReturnsNotifications() {
        when(jdbc.query(eq("SELECT * FROM notifications WHERE user_id = ? AND status = 'UNREAD' ORDER BY id DESC"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(notif));
        assertThat(service.handle(new ListUnreadNotificationsQuery(1L))).hasSize(1);
    }
}