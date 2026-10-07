package com.deepprotech.deepproject.iam.dto;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        String identityId,
        UUID userId,
        String username,
        String email,
        String displayName,
        List<String> globalRoles,
        List<String> globalPermissions,
        List<OrganizationMembership> organizations
) {
    public record OrganizationMembership(
            UUID organizationId,
            String identifier,
            String name,
            String role,
            List<String> permissions
    ) {}
}