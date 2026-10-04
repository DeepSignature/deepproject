package com.deepprotech.deepproject.organizations.api;

import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationCommand;

public interface UpdateOrganizationService {
    Organization handle(UpdateOrganizationCommand command);
}