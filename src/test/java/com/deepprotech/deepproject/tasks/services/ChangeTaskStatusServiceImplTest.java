package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;
import com.deepprotech.deepproject.tasks.constants.TaskStatus;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeTaskStatusServiceImplTest {

    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock TaskRepository taskRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ChangeTaskStatusServiceImpl service;

    @Test
    void changesStatus() {
        ChangeTaskStatusCommand cmd = new ChangeTaskStatusCommand(TASK_ID, TaskStatus.DONE);
        Task task = Task.builder().id(TASK_ID).title("Task").status("IN_PROGRESS").build();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        service.handle(cmd);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE.name());
        verify(taskRepository).save(task);
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenTaskNotFound() {
        ChangeTaskStatusCommand cmd = new ChangeTaskStatusCommand(UNKNOWN_ID, TaskStatus.DONE);
        when(taskRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
