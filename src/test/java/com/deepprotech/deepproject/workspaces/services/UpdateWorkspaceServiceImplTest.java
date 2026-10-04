package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateWorkspaceServiceImplTest {

    @Mock WorkspaceRepository workspaceRepository;
    @InjectMocks UpdateWorkspaceServiceImpl service;

    @Test
    void updatesWorkspaceReturnsIt() {
        UpdateWorkspaceCommand cmd = new UpdateWorkspaceCommand(1L, "New WS", "New Desc");
        Workspace ws = Workspace.builder().id(1L).name("Old WS").slug("ws").description("Old Desc").ownerId(1L).build();
        when(workspaceRepository.findById(1L)).thenReturn(Optional.of(ws));
        when(workspaceRepository.save(any(Workspace.class))).thenAnswer(inv -> inv.getArgument(0));

        Workspace result = service.handle(cmd);

        assertThat(result.getName()).isEqualTo("New WS");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        verify(workspaceRepository).save(ws);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateWorkspaceCommand cmd = new UpdateWorkspaceCommand(999L, "New WS", "New Desc");
        when(workspaceRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
