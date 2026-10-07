package com.deepprotech.deepproject.workspaces.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteWorkspaceCommand(@NotNull UUID workspaceId) {}
