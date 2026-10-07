package com.deepprotech.deepproject.organizations.dto;

import com.deepprotech.deepproject.core.Organization;

import java.util.UUID;

public record OrganizationResponse(UUID id, String identifier, String name, String description) {
    public static OrganizationResponse from(Organization org) {
        return new OrganizationResponse(org.getId(), org.getIdentifier(), org.getName(), org.getDescription());
    }
}