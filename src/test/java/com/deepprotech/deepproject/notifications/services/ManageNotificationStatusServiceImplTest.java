package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageNotificationStatusServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks ManageNotificationStatusServiceImpl service;

    private static final UUID NOTIF_ID = UUID.fromString("a0000013-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Test
    void marksNotificationRead() {
        Notification notif = Notification.builder().id(NOTIF_ID).userId(USER_ID).status("UNREAD").build();
        when(notificationRepository.findById(NOTIF_ID)).thenReturn(Optional.of(notif));
        service.handle(new MarkNotificationReadCommand(NOTIF_ID));
        verify(notificationRepository).save(notif);
    }

    @Test
    void marksAllNotificationsRead() {
        service.handle(new MarkAllNotificationsReadCommand(USER_ID));
        verify(notificationRepository).markAllAsReadByUserId(USER_ID);
    }
}
