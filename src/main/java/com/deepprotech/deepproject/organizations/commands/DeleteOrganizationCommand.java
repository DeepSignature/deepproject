package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotNull;

public record DeleteOrganizationCommand(@NotNull Long organizationId) {}