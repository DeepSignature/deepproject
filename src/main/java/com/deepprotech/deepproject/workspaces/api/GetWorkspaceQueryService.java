package com.deepprotech.deepproject.workspaces.api;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceByIdQuery;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceBySlugQuery;
import com.deepprotech.deepproject.workspaces.queries.ListUserWorkspacesQuery;
import com.deepprotech.deepproject.workspaces.queries.ListWorkspaceMembersQuery;

import java.util.List;

public interface GetWorkspaceQueryService {
    Workspace handle(GetWorkspaceByIdQuery query);
    Workspace handle(GetWorkspaceBySlugQuery query);
    List<Workspace> handle(ListUserWorkspacesQuery query);
    List<WorkspaceMember> handle(ListWorkspaceMembersQuery query);
}
