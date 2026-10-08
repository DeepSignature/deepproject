package com.deepprotech.deepproject.notifications.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.notifications.api.GetNotificationQueryService;
import com.deepprotech.deepproject.notifications.api.ManageNotificationStatusService;
import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;
import com.deepprotech.deepproject.notifications.dto.NotificationResponse;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ManageNotificationStatusService manageNotificationStatusService;
    private final GetNotificationQueryService getNotificationQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATION_READ')")
    public ResponseEntity<CursorPage<NotificationResponse>> list(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String cursor) {
        CursorPage<NotificationResponse> list = getNotificationQueryService.handle(new ListUserNotificationsQuery(userId, limit, cursor))
                .map(NotificationResponse::from);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATION_READ')")
    public ResponseEntity<CursorPage<NotificationResponse>> unread(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String cursor) {
        CursorPage<NotificationResponse> list = getNotificationQueryService.handle(new ListUnreadNotificationsQuery(userId, limit, cursor))
                .map(NotificationResponse::from);
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