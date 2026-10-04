package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.ChangeProjectStatusCommand;
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
class ChangeProjectStatusServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ChangeProjectStatusServiceImpl service;

    @Test
    void changesStatus() {
        Project project = Project.builder().id(1L).workspaceId(1L).name("P").description("D").status("ACTIVE").build();
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(project));

        Project result = service.handle(new ChangeProjectStatusCommand(1L, "COMPLETED"));

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(jdbc).update(eq("UPDATE projects SET status = ?, updated_at = ? WHERE id = ?"), eq("COMPLETED"), any(), eq(1L));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenProjectNotFound() {
        when(jdbc.query(eq("SELECT * FROM projects WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new ChangeProjectStatusCommand(999L, "COMPLETED")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}