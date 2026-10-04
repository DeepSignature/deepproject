package com.deepprotech.deepproject.notifications.api;

import com.deepprotech.deepproject.notifications.commands.MarkAllNotificationsReadCommand;
import com.deepprotech.deepproject.notifications.commands.MarkNotificationReadCommand;

public interface ManageNotificationStatusService {
    void handle(MarkNotificationReadCommand command);
    void handle(MarkAllNotificationsReadCommand command);
}
