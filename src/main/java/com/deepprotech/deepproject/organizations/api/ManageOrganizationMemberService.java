package com.deepprotech.deepproject.organizations.api;

import com.deepprotech.deepproject.organizations.commands.AddOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.RemoveOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationMemberRoleCommand;

public interface ManageOrganizationMemberService {
    void handle(AddOrganizationMemberCommand command);
    void handle(RemoveOrganizationMemberCommand command);
    void handle(UpdateOrganizationMemberRoleCommand command);
}