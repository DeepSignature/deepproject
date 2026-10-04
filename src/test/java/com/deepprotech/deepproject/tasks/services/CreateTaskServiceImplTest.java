package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.CreateTaskCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateTaskServiceImpl service;

    @Test
    void createsTask() {
        CreateTaskCommand cmd = new CreateTaskCommand(1L, "Task", "Description", "HIGH", "TASK");
        Task task = Task.builder().id(10L).projectId(1L).title("Task").description("Description").status("TODO").priority("HIGH").taskType("TASK").build();

        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(10L);
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(10L))).thenReturn(List.of(task));

        Task result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("Task");
        verify(jdbc).update(eq("INSERT INTO tasks (project_id, title, description, priority, task_type) VALUES (?, ?, ?, ?, ?)"),
                eq(1L), eq("Task"), eq("Description"), eq("HIGH"), eq("TASK"));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenTaskNotFoundAfterInsert() {
        CreateTaskCommand cmd = new CreateTaskCommand(1L, "Task", "Description", "HIGH", "TASK");
        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(10L);
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(10L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}