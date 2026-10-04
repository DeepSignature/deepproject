package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.api.CreateWorkspaceService;
import com.deepprotech.deepproject.workspaces.commands.CreateWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.events.WorkspaceCreatedEvent;
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
public class CreateWorkspaceServiceImpl implements CreateWorkspaceService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

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
    public Workspace handle(CreateWorkspaceCommand command) {
        jdbc.update("INSERT INTO workspaces (name, slug, description, owner_id, organization_id) VALUES (?, ?, ?, ?, ?)",
                command.name(), command.slug(), command.description(), command.ownerId(), command.organizationId());
        List<Workspace> list = jdbc.query("SELECT * FROM workspaces WHERE slug = ?", WS_MAPPER, command.slug());
        if (list.isEmpty()) throw new ResourceNotFoundException("Workspace not found: " + command.slug());
        Workspace ws = list.get(0);

        jdbc.update("INSERT INTO workspace_members (workspace_id, user_id, role) VALUES (?, ?, ?)",
                ws.getId(), command.ownerId(), "OWNER");
        eventPublisher.publishEvent(new WorkspaceCreatedEvent(ws.getId(), ws.getName(), ws.getOwnerId(), Instant.now()));
        log.info("workspace_created id={} name={}", ws.getId(), command.name());
        return ws;
    }
}