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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTaskQueryServiceImplTest {

    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");
    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID TASK_ID_2 = UUID.fromString("a0000009-0000-0000-0000-000000000002");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock TaskRepository taskRepository;
    @Mock TaskAssigneeRepository taskAssigneeRepository;
    @Mock TaskTagRepository taskTagRepository;
    @InjectMocks GetTaskQueryServiceImpl service;

    @Test
    void getByIdReturnsTask() {
        Task task = Task.builder().id(TASK_ID).title("Task").build();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        assertThat(service.handle(new GetTaskByIdQuery(TASK_ID))).isEqualTo(task);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(taskRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetTaskByIdQuery(UNKNOWN_ID))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByProjectReturnsTasks() {
        Task task = Task.builder().id(TASK_ID).build();
        when(taskRepository.findByProjectIdOrderByIdAsc(PROJECT_ID)).thenReturn(List.of(task));
        assertThat(service.handle(new ListTasksByProjectQuery(PROJECT_ID))).containsExactly(task);
    }

    @Test
    void listSubtasksReturnsTasks() {
        Task task = Task.builder().id(TASK_ID_2).parentTaskId(TASK_ID).build();
        when(taskRepository.findByParentTaskIdOrderByIdAsc(TASK_ID)).thenReturn(List.of(task));
        assertThat(service.handle(new ListSubtasksQuery(TASK_ID))).containsExactly(task);
    }

    @Test
    void listTaskAssigneesReturnsAssignees() {
        TaskAssignee assignee = TaskAssignee.builder().id(TASK_ID).taskId(TASK_ID).userId(USER_ID).build();
        when(taskAssigneeRepository.findByTaskId(TASK_ID)).thenReturn(List.of(assignee));
        assertThat(service.handle(new ListTaskAssigneesQuery(TASK_ID))).containsExactly(assignee);
    }

    @Test
    void listTaskTagsReturnsTags() {
        TaskTag tag = TaskTag.builder().id(TASK_ID).taskId(TASK_ID).tagName("backend").build();
        when(taskTagRepository.findByTaskId(TASK_ID)).thenReturn(List.of(tag));
        assertThat(service.handle(new ListTaskTagsQuery(TASK_ID))).containsExactly(tag);
    }
}
