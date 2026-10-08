package com.deepprotech.deepproject.organizations.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record PageOrganizationMembersQuery(@NotNull UUID organizationId, int limit, @Nullable String cursor) {}
