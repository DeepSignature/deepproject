package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateWorkspaceMemberRoleCommand(
        @NotNull Long workspaceId,
        @NotNull Long userId,
        @NotBlank String role
) {}
