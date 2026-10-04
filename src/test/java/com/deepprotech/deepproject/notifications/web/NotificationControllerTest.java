package com.deepprotech.deepproject.notifications.web;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.api.GetNotificationQueryService;
import com.deepprotech.deepproject.notifications.api.ManageNotificationStatusService;
import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock GetNotificationQueryService getNotificationQueryService;
    @Mock ManageNotificationStatusService manageNotificationStatusService;

    private MockMvc mockMvc;

    private final Notification notif = Notification.builder().id(1L).userId(1L).title("T").message("M").notificationType("INFO").status("UNREAD").build();

    @BeforeEach
    void setUp() {
        NotificationController controller = new NotificationController(manageNotificationStatusService, getNotificationQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listReturnsNotifications() throws Exception {
        when(getNotificationQueryService.handle(any(ListUserNotificationsQuery.class))).thenReturn(List.of(notif));
        mockMvc.perform(get("/api/notifications?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void unreadReturnsNotifications() throws Exception {
        when(getNotificationQueryService.handle(any(ListUnreadNotificationsQuery.class))).thenReturn(List.of(notif));
        mockMvc.perform(get("/api/notifications/unread?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void markAsReadReturnsOk() throws Exception {
        mockMvc.perform(patch("/api/notifications/{id}/read", 1L))
                .andExpect(status().isOk());
        verify(manageNotificationStatusService).handle(any(MarkNotificationReadCommand.class));
    }

    @Test
    void markAllAsReadReturnsOk() throws Exception {
        mockMvc.perform(patch("/api/notifications/read-all?userId=1"))
                .andExpect(status().isOk());
        verify(manageNotificationStatusService).handle(any(MarkAllNotificationsReadCommand.class));
    }
}
