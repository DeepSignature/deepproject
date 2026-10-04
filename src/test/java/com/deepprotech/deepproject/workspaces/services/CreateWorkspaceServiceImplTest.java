package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.commands.CreateWorkspaceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWorkspaceServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateWorkspaceServiceImpl service;

    @Test
    void createsWorkspace() {
        CreateWorkspaceCommand cmd = new CreateWorkspaceCommand("Workspace", "ws-slug", "Desc", 1L, 10L);
        Workspace ws = Workspace.builder().id(1L).name("Workspace").slug("ws-slug").description("Desc").ownerId(1L).organizationId(10L).build();

        when(jdbc.query(eq("SELECT * FROM workspaces WHERE slug = ?"), any(RowMapper.class), eq("ws-slug"))).thenReturn(List.of(ws));

        Workspace result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSlug()).isEqualTo("ws-slug");
        verify(jdbc).update(eq("INSERT INTO workspaces (name, slug, description, owner_id, organization_id) VALUES (?, ?, ?, ?, ?)"),
                eq("Workspace"), eq("ws-slug"), eq("Desc"), eq(1L), eq(10L));
        verify(jdbc).update(eq("INSERT INTO workspace_members (workspace_id, user_id, role) VALUES (?, ?, ?)"),
                eq(1L), eq(1L), eq("OWNER"));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenWorkspaceNotFoundAfterInsert() {
        CreateWorkspaceCommand cmd = new CreateWorkspaceCommand("Workspace", "ws-slug", "Desc", 1L, 10L);
        when(jdbc.query(eq("SELECT * FROM workspaces WHERE slug = ?"), any(RowMapper.class), eq("ws-slug"))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
