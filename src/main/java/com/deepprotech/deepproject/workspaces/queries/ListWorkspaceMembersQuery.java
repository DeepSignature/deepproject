package com.deepprotech.deepproject.workspaces.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListWorkspaceMembersQuery(@NotNull UUID workspaceId) {}
