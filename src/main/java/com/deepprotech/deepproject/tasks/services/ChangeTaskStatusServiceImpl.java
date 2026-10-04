package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.ChangeTaskStatusService;
import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;
import com.deepprotech.deepproject.tasks.events.TaskStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeTaskStatusServiceImpl implements ChangeTaskStatusService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    private static final RowMapper<Task> TASK_MAPPER = (rs, rowNum) -> Task.builder()
            .id(rs.getLong("id"))
            .projectId(rs.getLong("project_id"))
            .parentTaskId(rs.getObject("parent_task_id", Long.class))
            .title(rs.getString("title"))
            .description(rs.getString("description"))
            .status(rs.getString("status"))
            .priority(rs.getString("priority"))
            .taskType(rs.getString("task_type"))
            .build();

    @Override
    @Transactional
    public Task handle(ChangeTaskStatusCommand command) {
        List<Task> list = jdbc.query("SELECT * FROM tasks WHERE id = ?", TASK_MAPPER, command.taskId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Task", command.taskId());
        Task t = list.get(0);

        String oldStatus = t.getStatus();
        jdbc.update("UPDATE tasks SET status = ?, updated_at = ? WHERE id = ?",
                command.status(), Timestamp.from(Instant.now()), command.taskId());
        t.setStatus(command.status());

        eventPublisher.publishEvent(new TaskStatusChangedEvent(command.taskId(), oldStatus, command.status(), Instant.now()));
        log.info("task_status_changed id={} {} -> {}", command.taskId(), oldStatus, command.status());
        return t;
    }
}
