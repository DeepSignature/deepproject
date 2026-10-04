package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.CreateProjectCommand;
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
class CreateProjectServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateProjectServiceImpl service;

    @Test
    void createsProject() {
        CreateProjectCommand cmd = new CreateProjectCommand(1L, "Project", "Description");
        Project project = Project.builder().id(10L).workspaceId(1L).name("Project").description("Description").status("ACTIVE").build();

        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(10L);
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(10L))).thenReturn(List.of(project));

        Project result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Project");
        verify(jdbc).update(eq("INSERT INTO projects (workspace_id, name, description) VALUES (?, ?, ?)"),
                eq(1L), eq("Project"), eq("Description"));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenProjectNotFoundAfterInsert() {
        CreateProjectCommand cmd = new CreateProjectCommand(1L, "Project", "Description");
        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(10L);
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(10L))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}