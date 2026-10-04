package com.deepprotech.deepproject.organizations.api;

import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.commands.CreateOrganizationCommand;

public interface CreateOrganizationService {
    Organization handle(CreateOrganizationCommand command);
}