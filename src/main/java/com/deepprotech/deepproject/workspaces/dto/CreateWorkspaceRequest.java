package com.deepprotech.deepproject.workspaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 120) String slug,
        @Size(max = 2000) String description,
        @NotNull Long organizationId
) {}