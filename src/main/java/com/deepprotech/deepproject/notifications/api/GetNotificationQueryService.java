package com.deepprotech.deepproject.notifications.api;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;

import java.util.List;

public interface GetNotificationQueryService {
    List<Notification> handle(ListUserNotificationsQuery query);
    List<Notification> handle(ListUnreadNotificationsQuery query);
}
