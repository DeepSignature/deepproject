package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotNull;

public record RemoveOrganizationMemberCommand(
        @NotNull Long organizationId,
        @NotNull Long userId
) {}