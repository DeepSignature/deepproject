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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignTaskServiceImplTest {

    @Mock TaskAssigneeRepository taskAssigneeRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks AssignTaskServiceImpl service;

    @Test
    void assignsTask() {
        when(taskAssigneeRepository.findByTaskIdAndUserId(100L, 1L)).thenReturn(Optional.empty());
        service.handle(new AssignTaskUserCommand(100L, 1L));
        verify(taskAssigneeRepository).save(any(TaskAssignee.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void unassignsTask() {
        service.handle(new UnassignTaskUserCommand(100L, 1L));
        verify(taskAssigneeRepository).deleteByTaskIdAndUserId(100L, 1L);
    }
}
