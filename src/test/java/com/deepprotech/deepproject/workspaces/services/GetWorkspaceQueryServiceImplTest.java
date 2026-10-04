package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.queries.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWorkspaceQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetWorkspaceQueryServiceImpl service;

    private final Workspace ws = Workspace.builder().id(1L).name("WS").slug("ws").description("D").ownerId(1L).organizationId(10L).build();

    @Test
    void getByIdReturnsWorkspace() {
        when(jdbc.query(eq("SELECT * FROM workspaces WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(ws));
        assertThat(service.handle(new GetWorkspaceByIdQuery(1L)).getId()).isEqualTo(1L);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM workspaces WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetWorkspaceByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getBySlugReturnsWorkspace() {
        when(jdbc.query(eq("SELECT * FROM workspaces WHERE slug = ?"), any(RowMapper.class), eq("ws"))).thenReturn(List.of(ws));
        assertThat(service.handle(new GetWorkspaceBySlugQuery("ws")).getSlug()).isEqualTo("ws");
    }

    @Test
    void getBySlugThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM workspaces WHERE slug = ?"), any(RowMapper.class), eq("unknown"))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetWorkspaceBySlugQuery("unknown"))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listUserWorkspacesReturnsWorkspaces() {
        when(jdbc.query(any(String.class), any(RowMapper.class), eq(1L))).thenReturn(List.of(ws));
        assertThat(service.handle(new ListUserWorkspacesQuery(1L))).hasSize(1);
    }

    @Test
    void listWorkspaceMembersReturnsMembers() {
        WorkspaceMember member = WorkspaceMember.builder().id(1L).workspaceId(1L).userId(2L).role("OWNER").build();
        when(jdbc.query(eq("SELECT * FROM workspace_members WHERE workspace_id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(member));
        assertThat(service.handle(new ListWorkspaceMembersQuery(1L))).hasSize(1);
    }
}
