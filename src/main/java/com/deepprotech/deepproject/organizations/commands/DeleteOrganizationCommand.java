package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteOrganizationCommand(@NotNull UUID organizationId) {}