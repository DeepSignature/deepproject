package com.deepprotech.deepproject.notifications.web;

import com.deepprotech.deepproject.notifications.api.GetNotificationQueryService;
import com.deepprotech.deepproject.notifications.api.ManageNotificationStatusService;
import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;
import com.deepprotech.deepproject.notifications.dto.NotificationResponse;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ManageNotificationStatusService manageNotificationStatusService;
    private final GetNotificationQueryService getNotificationQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATION_READ')")
    public ResponseEntity<List<NotificationResponse>> list(@RequestParam UUID userId) {
        List<NotificationResponse> list = getNotificationQueryService.handle(new ListUserNotificationsQuery(userId))
                .stream()
                .map(NotificationResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATION_READ')")
    public ResponseEntity<List<NotificationResponse>> unread(@RequestParam UUID userId) {
        List<NotificationResponse> list = getNotificationQueryService.handle(new ListUnreadNotificationsQuery(userId))
                .stream()
                .map(NotificationResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATION_MANAGE')")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        manageNotificationStatusService.handle(new MarkNotificationReadCommand(id));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATION_MANAGE')")
    public ResponseEntity<Void> markAllAsRead(@RequestParam UUID userId) {
        manageNotificationStatusService.handle(new MarkAllNotificationsReadCommand(userId));
        return ResponseEntity.ok().build();
    }
}