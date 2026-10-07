package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateWorkspaceCommand(
        @NotNull UUID workspaceId,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description
) {}
