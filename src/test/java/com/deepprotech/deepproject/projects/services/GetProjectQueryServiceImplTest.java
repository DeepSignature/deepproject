package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;
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
class GetProjectQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetProjectQueryServiceImpl service;

    private final Project project = Project.builder().id(1L).workspaceId(1L).name("Project").description("Desc").status("ACTIVE").build();

    @Test
    void getByIdReturnsProject() {
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(project));
        assertThat(service.handle(new GetProjectByIdQuery(1L)).getId()).isEqualTo(1L);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetProjectByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByWorkspaceReturnsProjects() {
        when(jdbc.query(eq("SELECT * FROM projects WHERE workspace_id = ? ORDER BY id"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(project));
        assertThat(service.handle(new ListProjectsByWorkspaceQuery(1L))).hasSize(1);
    }
}