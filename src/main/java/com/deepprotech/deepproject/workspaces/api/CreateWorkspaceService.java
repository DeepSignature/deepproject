package com.deepprotech.deepproject.workspaces.api;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.commands.CreateWorkspaceCommand;

public interface CreateWorkspaceService {
    Workspace handle(CreateWorkspaceCommand command);
}
