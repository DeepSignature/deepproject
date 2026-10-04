package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceCommand;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateWorkspaceServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks UpdateWorkspaceServiceImpl service;

    @Test
    void updatesWorkspaceReturnsIt() {
        UpdateWorkspaceCommand cmd = new UpdateWorkspaceCommand(1L, "New WS", "New Desc");
        Workspace ws = Workspace.builder().id(1L).name("New WS").slug("ws").description("New Desc").ownerId(1L).build();
        when(jdbc.query(eq("SELECT * FROM workspaces WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(ws));
        Workspace result = service.handle(cmd);
        assertThat(result.getName()).isEqualTo("New WS");
        verify(jdbc).update(eq("UPDATE workspaces SET name = ?, description = ?, updated_at = ? WHERE id = ?"),
                eq("New WS"), eq("New Desc"), any(), eq(1L));
    }

    @Test
    void throwsWhenNotFound() {
        UpdateWorkspaceCommand cmd = new UpdateWorkspaceCommand(999L, "New WS", "New Desc");
        when(jdbc.query(eq("SELECT * FROM workspaces WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
