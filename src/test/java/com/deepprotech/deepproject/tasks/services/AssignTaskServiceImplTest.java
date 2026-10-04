package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.commands.AssignTaskUserCommand;
import com.deepprotech.deepproject.tasks.commands.UnassignTaskUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssignTaskServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks AssignTaskServiceImpl service;

    @Test
    void assignsTask() {
        service.handle(new AssignTaskUserCommand(1L, 2L));
        verify(jdbc).update(eq("INSERT INTO task_assignees (task_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING"), eq(1L), eq(2L));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void unassignsTask() {
        service.handle(new UnassignTaskUserCommand(1L, 2L));
        verify(jdbc).update(eq("DELETE FROM task_assignees WHERE task_id = ? AND user_id = ?"), eq(1L), eq(2L));
    }
}