package com.deepprotech.deepproject.iam.dto;

import com.deepprotech.deepproject.core.User;

public record UserResponse(Long id, String identityId, String username, String email, String displayName, boolean active) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getIdentityId(), user.getUsername(), user.getEmail(),
                user.getDisplayName(), user.isActive());
    }
}