package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddWorkspaceMemberCommand(
        @NotNull Long workspaceId,
        @NotNull Long userId,
        @NotBlank String role
) {}
