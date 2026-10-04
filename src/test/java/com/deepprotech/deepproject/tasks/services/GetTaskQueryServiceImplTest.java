package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.queries.*;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTaskQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetTaskQueryServiceImpl service;

    private final Task task = Task.builder().id(1L).projectId(1L).title("Task").description("Desc").status("TODO").priority("HIGH").taskType("TASK").build();

    @Test
    void getByIdReturnsTask() {
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(task));
        assertThat(service.handle(new GetTaskByIdQuery(1L)).getId()).isEqualTo(1L);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM tasks WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetTaskByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByProjectReturnsTasks() {
        when(jdbc.query(eq("SELECT * FROM tasks WHERE project_id = ? AND parent_task_id IS NULL ORDER BY id"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(task));
        assertThat(service.handle(new ListTasksByProjectQuery(1L))).hasSize(1);
    }

    @Test
    void listSubtasksReturnsTasks() {
        Task subtask = Task.builder().id(2L).projectId(1L).parentTaskId(1L).title("Subt").description("D").status("TODO").priority("MEDIUM").taskType("TASK").build();
        when(jdbc.query(eq("SELECT * FROM tasks WHERE parent_task_id = ? ORDER BY id"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(subtask));
        assertThat(service.handle(new ListSubtasksQuery(1L))).hasSize(1);
    }

    @Test
    void listTaskAssigneesReturnsAssignees() {
        TaskAssignee assignee = TaskAssignee.builder().id(1L).taskId(1L).userId(2L).build();
        when(jdbc.query(eq("SELECT * FROM task_assignees WHERE task_id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(assignee));
        assertThat(service.handle(new ListTaskAssigneesQuery(1L))).hasSize(1);
    }

    @Test
    void listTaskTagsReturnsTags() {
        TaskTag tag = TaskTag.builder().id(1L).taskId(1L).tagName("urgent").build();
        when(jdbc.query(eq("SELECT * FROM task_tags WHERE task_id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(tag));
        assertThat(service.handle(new ListTaskTagsQuery(1L))).hasSize(1);
    }
}