package com.deepprotech.deepproject.workspaces.dto;

import com.deepprotech.deepproject.core.WorkspaceMember;

import java.util.UUID;

public record WorkspaceMemberResponse(UUID id, UUID workspaceId, UUID userId, String role) {
    public static WorkspaceMemberResponse from(WorkspaceMember m) {
        return new WorkspaceMemberResponse(m.getId(), m.getWorkspaceId(), m.getUserId(), m.getRole());
    }
}
