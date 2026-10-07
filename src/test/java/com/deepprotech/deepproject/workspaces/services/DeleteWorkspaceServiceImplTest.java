package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.workspaces.commands.DeleteWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteWorkspaceServiceImplTest {

    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");

    @Mock WorkspaceRepository workspaceRepository;
    @InjectMocks DeleteWorkspaceServiceImpl service;

    @Test
    void deletesWorkspace() {
        service.handle(new DeleteWorkspaceCommand(WS_ID));
        verify(workspaceRepository).deleteById(WS_ID);
    }
}
