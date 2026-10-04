package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.UpdateTaskCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class UpdateTaskServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks UpdateTaskServiceImpl service;

    @Test
    void updatesTaskReturnsIt() {
        UpdateTaskCommand cmd = new UpdateTaskCommand(1L, "Updated", "Updated Desc", "LOW");
        Task task = Task.builder().id(1L).projectId(1L).title("Updated").description("Updated Desc").status("TODO").priority("LOW").taskType("TASK").build();
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(task));

        Task result = service.handle(cmd);

        assertThat(result.getTitle()).isEqualTo("Updated");
        assertThat(result.getPriority()).isEqualTo("LOW");
        verify(jdbc).update(eq("UPDATE tasks SET title = ?, description = ?, priority = ?, updated_at = ? WHERE id = ?"),
                eq("Updated"), eq("Updated Desc"), eq("LOW"), any(), eq(1L));
    }

    @Test
    void throwsWhenNotFound() {
        UpdateTaskCommand cmd = new UpdateTaskCommand(999L, "Updated", "Updated Desc", "LOW");
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}