package com.deepprotech.deepproject.workspaces.queries;

import jakarta.validation.constraints.NotNull;

public record GetWorkspaceByIdQuery(@NotNull Long workspaceId) {}
