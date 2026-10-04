package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.ChangeProjectStatusService;
import com.deepprotech.deepproject.projects.commands.ChangeProjectStatusCommand;
import com.deepprotech.deepproject.projects.events.ProjectStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeProjectStatusServiceImpl implements ChangeProjectStatusService {

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
    public Project handle(ChangeProjectStatusCommand command) {
        List<Project> list = jdbc.query("SELECT * FROM projects WHERE id = ?", MAPPER, command.projectId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Project", command.projectId());
        Project p = list.get(0);

        String oldStatus = p.getStatus();
        jdbc.update("UPDATE projects SET status = ?, updated_at = ? WHERE id = ?",
                command.status(), Timestamp.from(Instant.now()), command.projectId());
        p.setStatus(command.status());

        eventPublisher.publishEvent(new ProjectStatusChangedEvent(command.projectId(), oldStatus, command.status(), Instant.now()));
        log.info("project_status_changed id={} {} -> {}", command.projectId(), oldStatus, command.status());
        return p;
    }
}
