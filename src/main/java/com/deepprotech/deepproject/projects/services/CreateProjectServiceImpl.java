package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.CreateProjectService;
import com.deepprotech.deepproject.projects.commands.CreateProjectCommand;
import com.deepprotech.deepproject.projects.events.ProjectCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProjectServiceImpl implements CreateProjectService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    private static final RowMapper<Project> MAPPER = (rs, rowNum) -> Project.builder()
            .id(rs.getLong("id"))
            .workspaceId(rs.getLong("workspace_id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .status(rs.getString("status"))
            .build();

    @Override
    @Transactional
    public Project handle(CreateProjectCommand command) {
        jdbc.update("INSERT INTO projects (workspace_id, name, description) VALUES (?, ?, ?)",
                command.workspaceId(), command.name(), command.description());
        Long id = jdbc.queryForObject("SELECT LASTVAL()", Long.class);
        List<Project> list = jdbc.query("SELECT * FROM projects WHERE id = ?", MAPPER, id);
        if (list.isEmpty()) throw new ResourceNotFoundException("Project", id);
        Project p = list.get(0);

        eventPublisher.publishEvent(new ProjectCreatedEvent(p.getId(), p.getName(), p.getWorkspaceId(), Instant.now()));
        log.info("project_created id={} name={}", id, command.name());
        return p;
    }
}
