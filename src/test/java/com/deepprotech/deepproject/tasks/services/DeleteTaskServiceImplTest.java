package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.commands.DeleteTaskCommand;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteTaskServiceImplTest {

    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");

    @Mock TaskRepository taskRepository;
    @InjectMocks DeleteTaskServiceImpl service;

    @Test
    void deletesTask() {
        service.handle(new DeleteTaskCommand(TASK_ID));
        verify(taskRepository).deleteById(TASK_ID);
    }
}
