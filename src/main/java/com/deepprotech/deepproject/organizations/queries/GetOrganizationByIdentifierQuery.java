package com.deepprotech.deepproject.organizations.queries;

import jakarta.validation.constraints.NotBlank;

public record GetOrganizationByIdentifierQuery(@NotBlank String identifier) {}