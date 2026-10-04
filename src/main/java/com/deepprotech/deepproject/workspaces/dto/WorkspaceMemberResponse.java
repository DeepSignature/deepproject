package com.deepprotech.deepproject.workspaces.dto;

import com.deepprotech.deepproject.core.WorkspaceMember;

public record WorkspaceMemberResponse(Long id, Long workspaceId, Long userId, String role) {
    public static WorkspaceMemberResponse from(WorkspaceMember m) {
        return new WorkspaceMemberResponse(m.getId(), m.getWorkspaceId(), m.getUserId(), m.getRole());
    }
}
