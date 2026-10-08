package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceByIdQuery;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceBySlugQuery;
import com.deepprotech.deepproject.workspaces.queries.ListUserWorkspacesQuery;
import com.deepprotech.deepproject.workspaces.queries.ListWorkspaceMembersQuery;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceMemberRepository;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWorkspaceQueryServiceImplTest {

    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("a0000007-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock WorkspaceRepository workspaceRepository;
    @Mock WorkspaceMemberRepository workspaceMemberRepository;
    @InjectMocks GetWorkspaceQueryServiceImpl service;

    @Test
    void getByIdReturnsWorkspace() {
        Workspace ws = Workspace.builder().id(WS_ID).name("WS").build();
        when(workspaceRepository.findById(WS_ID)).thenReturn(Optional.of(ws));
        assertThat(service.handle(new GetWorkspaceByIdQuery(WS_ID))).isEqualTo(ws);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(workspaceRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetWorkspaceByIdQuery(UNKNOWN_ID))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getBySlugReturnsWorkspace() {
        Workspace ws = Workspace.builder().id(WS_ID).slug("my-ws").build();
        when(workspaceRepository.findBySlug("my-ws")).thenReturn(Optional.of(ws));
        assertThat(service.handle(new GetWorkspaceBySlugQuery("my-ws"))).isEqualTo(ws);
    }

    @Test
    void getBySlugThrowsWhenNotFound() {
        when(workspaceRepository.findBySlug("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetWorkspaceBySlugQuery("unknown"))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listUserWorkspacesReturnsWorkspaces() {
        Workspace ws = Workspace.builder().id(WS_ID).build();
        when(workspaceRepository.findWorkspacesByUserId(eq(USER_ID), any(Pageable.class))).thenReturn(List.of(ws));
        assertThat(service.handle(new ListUserWorkspacesQuery(USER_ID, 20, null)).items()).containsExactly(ws);
    }

    @Test
    void listWorkspaceMembersReturnsMembers() {
        WorkspaceMember member = WorkspaceMember.builder().id(MEMBER_ID).build();
        when(workspaceMemberRepository.findByWorkspaceId(WS_ID)).thenReturn(List.of(member));
        assertThat(service.handle(new ListWorkspaceMembersQuery(WS_ID))).containsExactly(member);
    }
}
