package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.commands.CreateNotificationCommand;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNotificationServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks CreateNotificationServiceImpl service;

    @Test
    void createsNotification() {
        CreateNotificationCommand cmd = new CreateNotificationCommand(1L, "Title", "Message", "INFO", "TASK", 100L);
        Notification notif = Notification.builder().id(10L).userId(1L).title("Title").message("Message").notificationType("INFO").status("UNREAD").entityType("TASK").entityId(100L).build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(notif);

        Notification result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("Title");
        verify(notificationRepository).save(any(Notification.class));
    }
}
