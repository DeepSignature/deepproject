package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.commands.DeleteTaskCommand;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteTaskServiceImplTest {

    @Mock TaskRepository taskRepository;
    @InjectMocks DeleteTaskServiceImpl service;

    @Test
    void deletesTask() {
        service.handle(new DeleteTaskCommand(100L));
        verify(taskRepository).deleteById(100L);
    }
}
