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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTaskServiceImplTest {

    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock TaskRepository taskRepository;
    @InjectMocks UpdateTaskServiceImpl service;

    @Test
    void updatesTaskReturnsIt() {
        UpdateTaskCommand cmd = new UpdateTaskCommand(TASK_ID, "New Title", "New Desc", "CRITICAL");
        Task task = Task.builder().id(TASK_ID).title("Old Title").description("Old Desc").priority("LOW").build();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.handle(cmd);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getPriority()).isEqualTo("CRITICAL");
        verify(taskRepository).save(task);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateTaskCommand cmd = new UpdateTaskCommand(UNKNOWN_ID, "Title", "Desc", "LOW");
        when(taskRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
