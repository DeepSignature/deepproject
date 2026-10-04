package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.workspaces.commands.DeleteWorkspaceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteWorkspaceServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks DeleteWorkspaceServiceImpl service;

    @Test
    void deletesWorkspace() {
        service.handle(new DeleteWorkspaceCommand(1L));
        verify(jdbc).update("DELETE FROM workspaces WHERE id = ?", 1L);
    }
}
