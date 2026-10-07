package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.commands.CreateNotificationCommand;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNotificationServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks CreateNotificationServiceImpl service;

    private static final UUID NOTIF_ID = UUID.fromString("a0000013-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID ENTITY_ID = UUID.fromString("a0000009-0000-0000-0000-000000000002");

    @Test
    void createsNotification() {
        CreateNotificationCommand cmd = new CreateNotificationCommand(USER_ID, "Title", "Message", "INFO", "TASK", ENTITY_ID);
        Notification notif = Notification.builder().id(NOTIF_ID).userId(USER_ID).title("Title").message("Message").notificationType("INFO").status("UNREAD").entityType("TASK").entityId(ENTITY_ID).build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(notif);

        Notification result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(NOTIF_ID);
        assertThat(result.getTitle()).isEqualTo("Title");
        verify(notificationRepository).save(any(Notification.class));
    }
}
