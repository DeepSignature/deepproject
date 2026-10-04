package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.api.UpdateWorkspaceService;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceCommand;
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
public class UpdateWorkspaceServiceImpl implements UpdateWorkspaceService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Workspace> WS_MAPPER = (rs, rowNum) -> Workspace.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .slug(rs.getString("slug"))
            .description(rs.getString("description"))
            .ownerId(rs.getLong("owner_id"))
            .organizationId(rs.getObject("organization_id", Long.class))
            .build();

    @Override
    @Transactional
    public Workspace handle(UpdateWorkspaceCommand command) {
        jdbc.update("UPDATE workspaces SET name = ?, description = ?, updated_at = ? WHERE id = ?",
                command.name(), command.description(), Timestamp.from(Instant.now()), command.workspaceId());
        log.info("workspace_updated id={}", command.workspaceId());
        List<Workspace> list = jdbc.query("SELECT * FROM workspaces WHERE id = ?", WS_MAPPER, command.workspaceId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Workspace", command.workspaceId());
        return list.get(0);
    }
}
