package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateOrganizationMemberRoleCommand(
        @NotNull UUID organizationId,
        @NotNull UUID userId,
        @NotBlank String role
) {}