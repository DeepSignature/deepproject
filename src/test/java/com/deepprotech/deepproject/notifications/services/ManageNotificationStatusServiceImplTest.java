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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageNotificationStatusServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks ManageNotificationStatusServiceImpl service;

    @Test
    void marksNotificationRead() {
        Notification notif = Notification.builder().id(10L).userId(1L).status("UNREAD").build();
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notif));
        service.handle(new MarkNotificationReadCommand(10L));
        verify(notificationRepository).save(notif);
    }

    @Test
    void marksAllNotificationsRead() {
        service.handle(new MarkAllNotificationsReadCommand(1L));
        verify(notificationRepository).markAllAsReadByUserId(1L);
    }
}
