package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotNull;

public record DeleteWorkspaceCommand(@NotNull Long workspaceId) {}
