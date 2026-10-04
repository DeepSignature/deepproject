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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWorkspaceQueryServiceImplTest {

    @Mock WorkspaceRepository workspaceRepository;
    @Mock WorkspaceMemberRepository workspaceMemberRepository;
    @InjectMocks GetWorkspaceQueryServiceImpl service;

    @Test
    void getByIdReturnsWorkspace() {
        Workspace ws = Workspace.builder().id(1L).name("WS").build();
        when(workspaceRepository.findById(1L)).thenReturn(Optional.of(ws));
        assertThat(service.handle(new GetWorkspaceByIdQuery(1L))).isEqualTo(ws);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(workspaceRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetWorkspaceByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getBySlugReturnsWorkspace() {
        Workspace ws = Workspace.builder().id(1L).slug("my-ws").build();
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
        Workspace ws = Workspace.builder().id(1L).build();
        when(workspaceRepository.findWorkspacesByUserId(1L)).thenReturn(List.of(ws));
        assertThat(service.handle(new ListUserWorkspacesQuery(1L))).containsExactly(ws);
    }

    @Test
    void listWorkspaceMembersReturnsMembers() {
        WorkspaceMember member = WorkspaceMember.builder().id(1L).build();
        when(workspaceMemberRepository.findByWorkspaceId(1L)).thenReturn(List.of(member));
        assertThat(service.handle(new ListWorkspaceMembersQuery(1L))).containsExactly(member);
    }
}
