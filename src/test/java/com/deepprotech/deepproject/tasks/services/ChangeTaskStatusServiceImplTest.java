package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeTaskStatusServiceImplTest {

    @Mock TaskRepository taskRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ChangeTaskStatusServiceImpl service;

    @Test
    void changesStatus() {
        ChangeTaskStatusCommand cmd = new ChangeTaskStatusCommand(100L, "DONE");
        Task task = Task.builder().id(100L).title("Task").status("IN_PROGRESS").build();
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.handle(cmd);

        assertThat(result.getStatus()).isEqualTo("DONE");
        verify(taskRepository).save(task);
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenTaskNotFound() {
        ChangeTaskStatusCommand cmd = new ChangeTaskStatusCommand(999L, "DONE");
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
