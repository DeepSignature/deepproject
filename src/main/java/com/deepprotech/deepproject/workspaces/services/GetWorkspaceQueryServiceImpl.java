package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.api.GetWorkspaceQueryService;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceByIdQuery;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceBySlugQuery;
import com.deepprotech.deepproject.workspaces.queries.ListUserWorkspacesQuery;
import com.deepprotech.deepproject.workspaces.queries.ListWorkspaceMembersQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWorkspaceQueryServiceImpl implements GetWorkspaceQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Workspace> WS_MAPPER = (rs, rowNum) -> Workspace.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .slug(rs.getString("slug"))
            .description(rs.getString("description"))
            .ownerId(rs.getLong("owner_id"))
            .organizationId(rs.getObject("organization_id", Long.class))
            .build();

    private static final RowMapper<WorkspaceMember> WM_MAPPER = (rs, rowNum) -> WorkspaceMember.builder()
            .id(rs.getLong("id"))
            .workspaceId(rs.getLong("workspace_id"))
            .userId(rs.getLong("user_id"))
            .role(rs.getString("role"))
            .build();

    @Override
    public Workspace handle(GetWorkspaceByIdQuery query) {
        List<Workspace> list = jdbc.query("SELECT * FROM workspaces WHERE id = ?", WS_MAPPER, query.workspaceId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Workspace", query.workspaceId());
        return list.get(0);
    }

    @Override
    public Workspace handle(GetWorkspaceBySlugQuery query) {
        List<Workspace> list = jdbc.query("SELECT * FROM workspaces WHERE slug = ?", WS_MAPPER, query.slug());
        if (list.isEmpty()) throw new ResourceNotFoundException("Workspace not found: " + query.slug());
        return list.get(0);
    }

    @Override
    public List<Workspace> handle(ListUserWorkspacesQuery query) {
        return jdbc.query("SELECT w.* FROM workspaces w INNER JOIN workspace_members wm ON w.id = wm.workspace_id WHERE wm.user_id = ?",
                WS_MAPPER, query.userId());
    }

    @Override
    public List<WorkspaceMember> handle(ListWorkspaceMembersQuery query) {
        return jdbc.query("SELECT * FROM workspace_members WHERE workspace_id = ?", WM_MAPPER, query.workspaceId());
    }
}