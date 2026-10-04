package com.deepprotech.deepproject.projects.queries;

import jakarta.validation.constraints.NotNull;

public record ListProjectsByWorkspaceQuery(@NotNull Long workspaceId) {}
