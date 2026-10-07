package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.commands.AddTaskTagCommand;
import com.deepprotech.deepproject.tasks.commands.RemoveTaskTagCommand;
import com.deepprotech.deepproject.tasks.repository.TaskTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageTaskTagServiceImplTest {

    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");

    @Mock TaskTagRepository taskTagRepository;
    @InjectMocks ManageTaskTagServiceImpl service;

    @Test
    void addsTag() {
        when(taskTagRepository.findByTaskIdAndTagName(TASK_ID, "backend")).thenReturn(Optional.empty());
        service.handle(new AddTaskTagCommand(TASK_ID, "backend"));
        verify(taskTagRepository).save(any(TaskTag.class));
    }

    @Test
    void removesTag() {
        service.handle(new RemoveTaskTagCommand(TASK_ID, "backend"));
        verify(taskTagRepository).deleteByTaskIdAndTagName(TASK_ID, "backend");
    }
}
