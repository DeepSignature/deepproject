package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveWorkspaceMemberCommand(@NotNull UUID workspaceId, @NotNull UUID userId) {}
