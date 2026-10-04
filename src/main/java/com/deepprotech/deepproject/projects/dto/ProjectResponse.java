package com.deepprotech.deepproject.projects.dto;

import com.deepprotech.deepproject.core.Project;

public record ProjectResponse(Long id, Long workspaceId, String name, String description, String status) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(p.getId(), p.getWorkspaceId(), p.getName(), p.getDescription(), p.getStatus());
    }
}
