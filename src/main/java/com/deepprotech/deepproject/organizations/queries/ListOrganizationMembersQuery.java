package com.deepprotech.deepproject.organizations.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListOrganizationMembersQuery(@NotNull UUID organizationId) {}