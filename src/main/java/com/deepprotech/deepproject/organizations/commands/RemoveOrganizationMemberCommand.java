package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveOrganizationMemberCommand(
        @NotNull UUID organizationId,
        @NotNull UUID userId
) {}