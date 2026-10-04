package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.api.GetNotificationQueryService;
import com.deepprotech.deepproject.notifications.queries.ListUnreadNotificationsQuery;
import com.deepprotech.deepproject.notifications.queries.ListUserNotificationsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNotificationQueryServiceImpl implements GetNotificationQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Notification> MAPPER = (rs, rowNum) -> Notification.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .title(rs.getString("title"))
            .message(rs.getString("message"))
            .notificationType(rs.getString("notification_type"))
            .status(rs.getString("status"))
            .entityType(rs.getString("entity_type"))
            .entityId(rs.getObject("entity_id", Long.class))
            .build();

    @Override
    public List<Notification> handle(ListUserNotificationsQuery query) {
        return jdbc.query("SELECT * FROM notifications WHERE user_id = ? ORDER BY id DESC", MAPPER, query.userId());
    }

    @Override
    public List<Notification> handle(ListUnreadNotificationsQuery query) {
        return jdbc.query("SELECT * FROM notifications WHERE user_id = ? AND status = 'UNREAD' ORDER BY id DESC", MAPPER, query.userId());
    }
}
