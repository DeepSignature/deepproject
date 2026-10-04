package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotNull;

public record RemoveWorkspaceMemberCommand(@NotNull Long workspaceId, @NotNull Long userId) {}
