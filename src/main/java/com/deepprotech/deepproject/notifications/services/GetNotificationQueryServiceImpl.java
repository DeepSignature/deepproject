package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.api.GetNotificationQueryService;
import com.deepprotech.deepproject.notifications.constants.NotificationStatus;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import com.deepprotech.deepproject.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
    public CursorPage<Notification> handle(ListUserNotificationsQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        List<Notification> notifications = key == null
                ? notificationRepository.findByUserId(query.userId(), pageable)
                : notificationRepository.findByUserIdBefore(query.userId(), key.createdAt(), key.id(), pageable);
        return CursorPages.build(notifications, query.limit(), Notification::getCreatedAt, Notification::getId);
    }

    @Override
    public CursorPage<Notification> handle(ListUnreadNotificationsQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        String status = NotificationStatus.UNREAD.name();
        List<Notification> notifications = key == null
                ? notificationRepository.findByUserIdAndStatus(query.userId(), status, pageable)
                : notificationRepository.findByUserIdAndStatusBefore(query.userId(), status, key.createdAt(), key.id(), pageable);
        return CursorPages.build(notifications, query.limit(), Notification::getCreatedAt, Notification::getId);
    }
}
