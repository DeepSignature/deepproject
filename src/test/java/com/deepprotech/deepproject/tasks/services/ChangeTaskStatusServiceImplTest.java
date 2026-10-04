package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;
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
class ChangeTaskStatusServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ChangeTaskStatusServiceImpl service;

    @Test
    void changesStatus() {
        Task task = Task.builder().id(1L).projectId(1L).title("T").description("D").status("TODO").priority("HIGH").taskType("TASK").build();
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(task));

        Task result = service.handle(new ChangeTaskStatusCommand(1L, "IN_PROGRESS"));

        assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
        verify(jdbc).update(eq("UPDATE tasks SET status = ?, updated_at = ? WHERE id = ?"), eq("IN_PROGRESS"), any(), eq(1L));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenTaskNotFound() {
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new ChangeTaskStatusCommand(999L, "DONE")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}