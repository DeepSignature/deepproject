package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.UpdateProjectCommand;
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
class UpdateProjectServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks UpdateProjectServiceImpl service;

    @Test
    void updatesProjectReturnsIt() {
        UpdateProjectCommand cmd = new UpdateProjectCommand(1L, "Updated", "Updated Desc");
        Project project = Project.builder().id(1L).workspaceId(1L).name("Updated").description("Updated Desc").status("ACTIVE").build();
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(project));

        Project result = service.handle(cmd);

        assertThat(result.getName()).isEqualTo("Updated");
        verify(jdbc).update(eq("UPDATE projects SET name = ?, description = ?, updated_at = ? WHERE id = ?"),
                eq("Updated"), eq("Updated Desc"), any(), eq(1L));
    }

    @Test
    void throwsWhenNotFound() {
        UpdateProjectCommand cmd = new UpdateProjectCommand(999L, "Updated", "Updated Desc");
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}