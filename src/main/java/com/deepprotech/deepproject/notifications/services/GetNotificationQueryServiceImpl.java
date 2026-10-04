package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.api.GetNotificationQueryService;
import com.deepprotech.deepproject.notifications.constants.NotificationStatus;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNotificationQueryServiceImpl implements GetNotificationQueryService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> handle(ListUserNotificationsQuery query) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(query.userId());
    }

    @Override
    public List<Notification> handle(ListUnreadNotificationsQuery query) {
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(query.userId(), NotificationStatus.UNREAD.name());
    }
}
