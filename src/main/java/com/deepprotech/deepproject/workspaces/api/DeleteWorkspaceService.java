package com.deepprotech.deepproject.workspaces.api;

import com.deepprotech.deepproject.workspaces.commands.DeleteWorkspaceCommand;

public interface DeleteWorkspaceService {
    void handle(DeleteWorkspaceCommand command);
}
