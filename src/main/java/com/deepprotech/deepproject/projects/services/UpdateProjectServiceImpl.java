package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.UpdateProjectService;
import com.deepprotech.deepproject.projects.commands.UpdateProjectCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UpdateProjectServiceImpl implements UpdateProjectService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Project> MAPPER = (rs, rowNum) -> Project.builder()
            .id(rs.getLong("id"))
            .workspaceId(rs.getLong("workspace_id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .status(rs.getString("status"))
            .build();

    @Override
    @Transactional
    public Project handle(UpdateProjectCommand command) {
        jdbc.update("UPDATE projects SET name = ?, description = ?, updated_at = ? WHERE id = ?",
                command.name(), command.description(), Timestamp.from(Instant.now()), command.projectId());
        log.info("project_updated id={}", command.projectId());
        List<Project> list = jdbc.query("SELECT * FROM projects WHERE id = ?", MAPPER, command.projectId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Project", command.projectId());
        return list.get(0);
    }
}
