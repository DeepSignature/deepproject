package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.commands.CreateNotificationCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNotificationServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks CreateNotificationServiceImpl service;

    @Test
    void createsNotification() {
        CreateNotificationCommand cmd = new CreateNotificationCommand(1L, "Title", "Message", "INFO", "TASK", 10L);
        Notification notif = Notification.builder().id(100L).userId(1L).title("Title").message("Message").notificationType("INFO").status("UNREAD").entityType("TASK").entityId(10L).build();

        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(100L);
        when(jdbc.query(eq("SELECT * FROM notifications WHERE id = ?"), any(RowMapper.class), eq(100L))).thenReturn(List.of(notif));

        Notification result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getTitle()).isEqualTo("Title");
        verify(jdbc).update(any(String.class), eq(1L), eq("Title"), eq("Message"), eq("INFO"), eq("TASK"), eq(10L));
    }

    @Test
    void throwsWhenNotificationNotFoundAfterInsert() {
        CreateNotificationCommand cmd = new CreateNotificationCommand(1L, "T", "M", "INFO", "TASK", 10L);
        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(100L);
        when(jdbc.query(eq("SELECT * FROM notifications WHERE id = ?"), any(RowMapper.class), eq(100L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}