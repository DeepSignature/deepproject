package com.deepprotech.deepproject.projects.api;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;

public interface GetProjectQueryService {
    Project handle(GetProjectByIdQuery query);
    CursorPage<Project> handle(ListProjectsByWorkspaceQuery query);
}
