package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrganizationMemberRoleCommand(
        @NotNull Long organizationId,
        @NotNull Long userId,
        @NotBlank String role
) {}