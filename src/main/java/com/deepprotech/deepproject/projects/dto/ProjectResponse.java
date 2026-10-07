package com.deepprotech.deepproject.projects.dto;

import com.deepprotech.deepproject.core.Project;

import java.util.UUID;

public record ProjectResponse(UUID id, UUID workspaceId, String name, String description, String status) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(p.getId(), p.getWorkspaceId(), p.getName(), p.getDescription(), p.getStatus());
    }
}
