package com.deepprotech.deepproject.organizations.api;

import com.deepprotech.deepproject.organizations.commands.DeleteOrganizationCommand;

public interface DeleteOrganizationService {
    void handle(DeleteOrganizationCommand command);
}