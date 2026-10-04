package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateWorkspaceCommand(
        @NotNull Long workspaceId,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description
) {}
