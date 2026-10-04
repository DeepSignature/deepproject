package com.deepprotech.deepproject.organizations.queries;

import jakarta.validation.constraints.NotNull;

public record GetOrganizationByIdQuery(@NotNull Long organizationId) {}