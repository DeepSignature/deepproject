package com.deepprotech.deepproject.workspaces.dto;

import com.deepprotech.deepproject.core.Workspace;

import java.util.UUID;

public record WorkspaceResponse(UUID id, String name, String slug, String description, UUID ownerId, UUID organizationId) {
    public static WorkspaceResponse from(Workspace w) {
        return new WorkspaceResponse(w.getId(), w.getName(), w.getSlug(), w.getDescription(), w.getOwnerId(), w.getOrganizationId());
    }
}