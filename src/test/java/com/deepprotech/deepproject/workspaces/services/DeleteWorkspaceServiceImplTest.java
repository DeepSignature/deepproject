package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.workspaces.commands.DeleteWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteWorkspaceServiceImplTest {

    @Mock WorkspaceRepository workspaceRepository;
    @InjectMocks DeleteWorkspaceServiceImpl service;

    @Test
    void deletesWorkspace() {
        service.handle(new DeleteWorkspaceCommand(1L));
        verify(workspaceRepository).deleteById(1L);
    }
}
