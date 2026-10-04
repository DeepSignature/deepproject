package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNotificationQueryServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks GetNotificationQueryServiceImpl service;

    @Test
    void listUserNotificationsReturnsNotifications() {
        Notification n = Notification.builder().id(1L).userId(10L).build();
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(n));
        assertThat(service.handle(new ListUserNotificationsQuery(10L))).containsExactly(n);
    }

    @Test
    void listUnreadReturnsNotifications() {
        Notification n = Notification.builder().id(1L).userId(10L).status("UNREAD").build();
        when(notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(10L, "UNREAD")).thenReturn(List.of(n));
        assertThat(service.handle(new ListUnreadNotificationsQuery(10L))).containsExactly(n);
    }
}
