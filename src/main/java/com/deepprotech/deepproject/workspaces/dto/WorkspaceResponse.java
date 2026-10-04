package com.deepprotech.deepproject.workspaces.dto;

import com.deepprotech.deepproject.core.Workspace;

public record WorkspaceResponse(Long id, String name, String slug, String description, Long ownerId, Long organizationId) {
    public static WorkspaceResponse from(Workspace w) {
        return new WorkspaceResponse(w.getId(), w.getName(), w.getSlug(), w.getDescription(), w.getOwnerId(), w.getOrganizationId());
    }
}