package com.deepprotech.deepproject.organizations.dto;

import com.deepprotech.deepproject.core.OrganizationMember;

import java.util.UUID;

public record OrganizationMemberResponse(UUID id, UUID organizationId, UUID userId, String role) {
    public static OrganizationMemberResponse from(OrganizationMember member) {
        return new OrganizationMemberResponse(member.getId(), member.getOrganizationId(), member.getUserId(), member.getRole());
    }
}