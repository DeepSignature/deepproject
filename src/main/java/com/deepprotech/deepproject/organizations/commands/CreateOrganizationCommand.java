package com.deepprotech.deepproject.organizations.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrganizationCommand(
        @NotBlank @Size(max = 100) String identifier,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description,
        @NotBlank Long createdByUserId
) {}