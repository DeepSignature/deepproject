package com.deepprotech.deepproject.notifications.api;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;

public interface GetNotificationQueryService {
    CursorPage<Notification> handle(ListUserNotificationsQuery query);
    CursorPage<Notification> handle(ListUnreadNotificationsQuery query);
}
