package com.deepprotech.deepproject.workspaces.api;

import com.deepprotech.deepproject.workspaces.commands.AddWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.RemoveWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceMemberRoleCommand;

public interface ManageWorkspaceMemberService {
    void handle(AddWorkspaceMemberCommand command);
    void handle(RemoveWorkspaceMemberCommand command);
    void handle(UpdateWorkspaceMemberRoleCommand command);
}
