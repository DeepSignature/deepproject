package com.deepprotech.deepproject.notifications.api;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.commands.CreateNotificationCommand;

public interface CreateNotificationService {
    Notification handle(CreateNotificationCommand command);
}
