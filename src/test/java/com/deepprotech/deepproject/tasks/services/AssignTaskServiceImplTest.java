package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.tasks.commands.AssignTaskUserCommand;
import com.deepprotech.deepproject.tasks.commands.UnassignTaskUserCommand;
import com.deepprotech.deepproject.tasks.repository.TaskAssigneeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignTaskServiceImplTest {

    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Mock TaskAssigneeRepository taskAssigneeRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks AssignTaskServiceImpl service;

    @Test
    void assignsTask() {
        when(taskAssigneeRepository.findByTaskIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.empty());
        service.handle(new AssignTaskUserCommand(TASK_ID, USER_ID));
        verify(taskAssigneeRepository).save(any(TaskAssignee.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void unassignsTask() {
        service.handle(new UnassignTaskUserCommand(TASK_ID, USER_ID));
        verify(taskAssigneeRepository).deleteByTaskIdAndUserId(TASK_ID, USER_ID);
    }
}
