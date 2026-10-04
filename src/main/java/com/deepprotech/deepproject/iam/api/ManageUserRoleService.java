package com.deepprotech.deepproject.iam.api;

import com.deepprotech.deepproject.iam.commands.AssignUserRoleCommand;
import com.deepprotech.deepproject.iam.commands.RemoveUserRoleCommand;

public interface ManageUserRoleService {
    void handle(AssignUserRoleCommand command);
    void handle(RemoveUserRoleCommand command);
}
