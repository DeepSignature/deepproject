package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.CreateTaskCommand;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskServiceImplTest {

    @Mock TaskRepository taskRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateTaskServiceImpl service;

    @Test
    void createsTask() {
        CreateTaskCommand cmd = new CreateTaskCommand(10L, "Task 1", "Desc", "HIGH", "BUG");
        Task task = Task.builder().id(100L).projectId(10L).title("Task 1").description("Desc").priority("HIGH").taskType("BUG").status("TODO").build();
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getTitle()).isEqualTo("Task 1");
        verify(taskRepository).save(any(Task.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
