package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.GetProjectQueryService;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;
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
public class GetProjectQueryServiceImpl implements GetProjectQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Project> MAPPER = (rs, rowNum) -> Project.builder()
            .id(rs.getLong("id"))
            .workspaceId(rs.getLong("workspace_id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .status(rs.getString("status"))
            .build();

    @Override
    public Project handle(GetProjectByIdQuery query) {
        List<Project> list = jdbc.query("SELECT * FROM projects WHERE id = ?", MAPPER, query.projectId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Project", query.projectId());
        return list.get(0);
    }

    @Override
    public List<Project> handle(ListProjectsByWorkspaceQuery query) {
        return jdbc.query("SELECT * FROM projects WHERE workspace_id = ? ORDER BY id", MAPPER, query.workspaceId());
    }
}
