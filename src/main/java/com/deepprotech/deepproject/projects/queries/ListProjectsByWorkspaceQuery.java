package com.deepprotech.deepproject.projects.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListProjectsByWorkspaceQuery(@NotNull UUID workspaceId) {}
