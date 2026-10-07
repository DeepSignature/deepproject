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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateWorkspaceServiceImplTest {

    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock WorkspaceRepository workspaceRepository;
    @InjectMocks UpdateWorkspaceServiceImpl service;

    @Test
    void updatesWorkspaceReturnsIt() {
        UpdateWorkspaceCommand cmd = new UpdateWorkspaceCommand(WS_ID, "New WS", "New Desc");
        Workspace ws = Workspace.builder().id(WS_ID).name("Old WS").slug("ws").description("Old Desc").ownerId(USER_ID).build();
        when(workspaceRepository.findById(WS_ID)).thenReturn(Optional.of(ws));
        when(workspaceRepository.save(any(Workspace.class))).thenAnswer(inv -> inv.getArgument(0));

        Workspace result = service.handle(cmd);

        assertThat(result.getName()).isEqualTo("New WS");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        verify(workspaceRepository).save(ws);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateWorkspaceCommand cmd = new UpdateWorkspaceCommand(UNKNOWN_ID, "New WS", "New Desc");
        when(workspaceRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
