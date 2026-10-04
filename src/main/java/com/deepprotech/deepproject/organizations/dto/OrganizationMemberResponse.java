package com.deepprotech.deepproject.organizations.dto;

import com.deepprotech.deepproject.core.OrganizationMember;

public record OrganizationMemberResponse(Long id, Long organizationId, Long userId, String role) {
    public static OrganizationMemberResponse from(OrganizationMember member) {
        return new OrganizationMemberResponse(member.getId(), member.getOrganizationId(), member.getUserId(), member.getRole());
    }
}