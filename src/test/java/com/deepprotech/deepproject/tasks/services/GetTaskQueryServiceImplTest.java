package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.queries.GetTaskByIdQuery;
import com.deepprotech.deepproject.tasks.queries.ListSubtasksQuery;
import com.deepprotech.deepproject.tasks.queries.ListTaskAssigneesQuery;
import com.deepprotech.deepproject.tasks.queries.ListTaskTagsQuery;
import com.deepprotech.deepproject.tasks.queries.ListTasksByProjectQuery;
import com.deepprotech.deepproject.tasks.repository.TaskAssigneeRepository;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import com.deepprotech.deepproject.tasks.repository.TaskTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTaskQueryServiceImplTest {

    @Mock TaskRepository taskRepository;
    @Mock TaskAssigneeRepository taskAssigneeRepository;
    @Mock TaskTagRepository taskTagRepository;
    @InjectMocks GetTaskQueryServiceImpl service;

    @Test
    void getByIdReturnsTask() {
        Task task = Task.builder().id(100L).title("Task").build();
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        assertThat(service.handle(new GetTaskByIdQuery(100L))).isEqualTo(task);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetTaskByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByProjectReturnsTasks() {
        Task task = Task.builder().id(100L).build();
        when(taskRepository.findByProjectIdOrderByIdAsc(10L)).thenReturn(List.of(task));
        assertThat(service.handle(new ListTasksByProjectQuery(10L))).containsExactly(task);
    }

    @Test
    void listSubtasksReturnsTasks() {
        Task task = Task.builder().id(101L).parentTaskId(100L).build();
        when(taskRepository.findByParentTaskIdOrderByIdAsc(100L)).thenReturn(List.of(task));
        assertThat(service.handle(new ListSubtasksQuery(100L))).containsExactly(task);
    }

    @Test
    void listTaskAssigneesReturnsAssignees() {
        TaskAssignee assignee = TaskAssignee.builder().id(1L).taskId(100L).userId(2L).build();
        when(taskAssigneeRepository.findByTaskId(100L)).thenReturn(List.of(assignee));
        assertThat(service.handle(new ListTaskAssigneesQuery(100L))).containsExactly(assignee);
    }

    @Test
    void listTaskTagsReturnsTags() {
        TaskTag tag = TaskTag.builder().id(1L).taskId(100L).tagName("backend").build();
        when(taskTagRepository.findByTaskId(100L)).thenReturn(List.of(tag));
        assertThat(service.handle(new ListTaskTagsQuery(100L))).containsExactly(tag);
    }
}
