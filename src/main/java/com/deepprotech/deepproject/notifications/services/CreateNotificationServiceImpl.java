package com.deepprotech.deepproject.notifications.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Notification;
import com.deepprotech.deepproject.notifications.api.CreateNotificationService;
import com.deepprotech.deepproject.notifications.commands.CreateNotificationCommand;
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
public class CreateNotificationServiceImpl implements CreateNotificationService {

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
    @Transactional
    public Notification handle(CreateNotificationCommand command) {
        jdbc.update(
                "INSERT INTO notifications (user_id, title, message, notification_type, entity_type, entity_id) VALUES (?, ?, ?, ?, ?, ?)",
                command.userId(), command.title(), command.message(), command.notificationType(),
                command.entityType(), command.entityId());
        Long id = jdbc.queryForObject("SELECT LASTVAL()", Long.class);
        log.info("notification_created id={} userId={} type={}", id, command.userId(), command.notificationType());
        List<Notification> list = jdbc.query("SELECT * FROM notifications WHERE id = ?", MAPPER, id);
        if (list.isEmpty()) throw new ResourceNotFoundException("Notification", id);
        return list.get(0);
    }
}
