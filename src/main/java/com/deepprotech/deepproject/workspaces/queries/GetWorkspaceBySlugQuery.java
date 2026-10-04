package com.deepprotech.deepproject.workspaces.queries;

import jakarta.validation.constraints.NotBlank;

public record GetWorkspaceBySlugQuery(@NotBlank String slug) {}
