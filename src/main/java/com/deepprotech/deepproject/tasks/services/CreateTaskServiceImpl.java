package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.CreateTaskService;
import com.deepprotech.deepproject.tasks.commands.CreateTaskCommand;
import com.deepprotech.deepproject.tasks.events.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTaskServiceImpl implements CreateTaskService {

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
    public Task handle(CreateTaskCommand command) {
        jdbc.update("INSERT INTO tasks (project_id, title, description, priority, task_type) VALUES (?, ?, ?, ?, ?)",
                command.projectId(), command.title(), command.description(), command.priority(), command.taskType());
        Long id = jdbc.queryForObject("SELECT LASTVAL()", Long.class);
        List<Task> list = jdbc.query("SELECT * FROM tasks WHERE id = ?", TASK_MAPPER, id);
        if (list.isEmpty()) throw new ResourceNotFoundException("Task", id);
        Task t = list.get(0);

        eventPublisher.publishEvent(new TaskCreatedEvent(t.getId(), t.getTitle(), t.getProjectId(), Instant.now()));
        log.info("task_created id={} title={}", id, command.title());
        return t;
    }
}
