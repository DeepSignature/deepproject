package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.UpdateTaskService;
import com.deepprotech.deepproject.tasks.commands.UpdateTaskCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UpdateTaskServiceImpl implements UpdateTaskService {

    private final JdbcTemplate jdbc;

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
    public Task handle(UpdateTaskCommand command) {
        jdbc.update("UPDATE tasks SET title = ?, description = ?, priority = ?, updated_at = ? WHERE id = ?",
                command.title(), command.description(), command.priority(), Timestamp.from(Instant.now()), command.taskId());
        log.info("task_updated id={}", command.taskId());
        List<Task> list = jdbc.query("SELECT * FROM tasks WHERE id = ?", TASK_MAPPER, command.taskId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Task", command.taskId());
        return list.get(0);
    }
}
