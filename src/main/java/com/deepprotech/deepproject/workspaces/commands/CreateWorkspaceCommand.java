package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceCommand(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 120) String slug,
        @Size(max = 2000) String description,
        @NotNull Long ownerId,
        @NotNull Long organizationId
) {}