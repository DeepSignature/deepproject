package com.deepprotech.deepproject.projects.api;

import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;

import java.util.List;

public interface GetProjectQueryService {
    Project handle(GetProjectByIdQuery query);
    List<Project> handle(ListProjectsByWorkspaceQuery query);
}
