package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.commands.DeleteTaskCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteTaskServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks DeleteTaskServiceImpl service;

    @Test
    void deletesTask() {
        service.handle(new DeleteTaskCommand(1L));
        verify(jdbc).update("DELETE FROM tasks WHERE id = ?", 1L);
    }
}