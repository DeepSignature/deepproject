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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNotificationQueryServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks GetNotificationQueryServiceImpl service;

    private static final UUID NOTIF_ID = UUID.fromString("a0000013-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Test
    void listUserNotificationsReturnsNotifications() {
        Notification n = Notification.builder().id(NOTIF_ID).userId(USER_ID).build();
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(USER_ID)).thenReturn(List.of(n));
        assertThat(service.handle(new ListUserNotificationsQuery(USER_ID))).containsExactly(n);
    }

    @Test
    void listUnreadReturnsNotifications() {
        Notification n = Notification.builder().id(NOTIF_ID).userId(USER_ID).status("UNREAD").build();
        when(notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(USER_ID, "UNREAD")).thenReturn(List.of(n));
        assertThat(service.handle(new ListUnreadNotificationsQuery(USER_ID))).containsExactly(n);
    }
}
