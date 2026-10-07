package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddWorkspaceMemberCommand(
        @NotNull UUID workspaceId,
        @NotNull UUID userId,
        @NotBlank String role
) {}
