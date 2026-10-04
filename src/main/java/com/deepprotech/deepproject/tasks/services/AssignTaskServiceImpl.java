package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.api.AssignTaskService;
import com.deepprotech.deepproject.tasks.commands.AssignTaskUserCommand;
import com.deepprotech.deepproject.tasks.commands.UnassignTaskUserCommand;
import com.deepprotech.deepproject.tasks.events.TaskAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignTaskServiceImpl implements AssignTaskService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(AssignTaskUserCommand command) {
        jdbc.update("INSERT INTO task_assignees (task_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                command.taskId(), command.userId());
        eventPublisher.publishEvent(new TaskAssignedEvent(command.taskId(), command.userId(), Instant.now()));
        log.info("task_assigned taskId={} userId={}", command.taskId(), command.userId());
    }

    @Override
    @Transactional
    public void handle(UnassignTaskUserCommand command) {
        jdbc.update("DELETE FROM task_assignees WHERE task_id = ? AND user_id = ?",
                command.taskId(), command.userId());
        log.info("task_unassigned taskId={} userId={}", command.taskId(), command.userId());
    }
}
