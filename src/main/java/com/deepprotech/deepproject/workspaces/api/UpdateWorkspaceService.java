package com.deepprotech.deepproject.workspaces.api;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceCommand;

public interface UpdateWorkspaceService {
    Workspace handle(UpdateWorkspaceCommand command);
}
