package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.projects.commands.DeleteProjectCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteProjectServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks DeleteProjectServiceImpl service;

    @Test
    void deletesProject() {
        service.handle(new DeleteProjectCommand(1L));
        verify(jdbc).update("DELETE FROM projects WHERE id = ?", 1L);
    }
}