package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.commands.CreateWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceMemberRepository;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWorkspaceServiceImplTest {

    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID ORG_ID = UUID.fromString("a0000003-0000-0000-0000-000000000001");

    @Mock WorkspaceRepository workspaceRepository;
    @Mock WorkspaceMemberRepository workspaceMemberRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateWorkspaceServiceImpl service;

    @Test
    void createsWorkspace() {
        CreateWorkspaceCommand cmd = new CreateWorkspaceCommand("Workspace", "ws-slug", "Desc", USER_ID, ORG_ID);
        Workspace ws = Workspace.builder().id(WS_ID).name("Workspace").slug("ws-slug").description("Desc").ownerId(USER_ID).organizationId(ORG_ID).build();

        when(workspaceRepository.save(any(Workspace.class))).thenReturn(ws);

        Workspace result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(WS_ID);
        assertThat(result.getSlug()).isEqualTo("ws-slug");
        verify(workspaceRepository).save(any(Workspace.class));
        verify(workspaceMemberRepository).save(any(WorkspaceMember.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
