package com.deepprotech.deepproject.workspaces.queries;

import jakarta.validation.constraints.NotNull;

public record ListWorkspaceMembersQuery(@NotNull Long workspaceId) {}
