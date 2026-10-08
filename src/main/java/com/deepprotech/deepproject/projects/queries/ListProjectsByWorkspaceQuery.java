package com.deepprotech.deepproject.projects.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListProjectsByWorkspaceQuery(@NotNull UUID workspaceId, int limit, @Nullable String cursor) {}
