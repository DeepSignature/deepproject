package com.deepprotech.deepproject.organizations.queries;

import jakarta.validation.constraints.NotNull;

public record ListUserOrganizationsQuery(@NotNull Long userId) {}