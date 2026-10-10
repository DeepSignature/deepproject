package com.deepprotech.deepproject.iam.queries;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

public record GetUsersByIdsQuery(@NotEmpty Set<UUID> userIds) {}
