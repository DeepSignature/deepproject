package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.UpdateTaskCommand;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTaskServiceImplTest {

    @Mock TaskRepository taskRepository;
    @InjectMocks UpdateTaskServiceImpl service;

    @Test
    void updatesTaskReturnsIt() {
        UpdateTaskCommand cmd = new UpdateTaskCommand(100L, "New Title", "New Desc", "CRITICAL");
        Task task = Task.builder().id(100L).title("Old Title").description("Old Desc").priority("LOW").build();
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.handle(cmd);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getPriority()).isEqualTo("CRITICAL");
        verify(taskRepository).save(task);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateTaskCommand cmd = new UpdateTaskCommand(999L, "Title", "Desc", "LOW");
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
