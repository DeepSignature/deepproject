package com.deepprotech.deepproject.iam.dto;

import java.util.List;

public record UserProfileResponse(
        String identityId,
        Long userId,
        String username,
        String email,
        String displayName,
        List<String> globalRoles,
        List<String> globalPermissions,
        List<OrganizationMembership> organizations
) {
    public record OrganizationMembership(
            Long organizationId,
            String identifier,
            String name,
            String role,
            List<String> permissions
    ) {}
}