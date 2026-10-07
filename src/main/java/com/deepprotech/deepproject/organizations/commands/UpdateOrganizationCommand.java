package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateOrganizationCommand(
        @NotNull UUID organizationId,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description
) {}