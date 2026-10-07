package com.deepprotech.deepproject.iam.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetUserByIdQuery(@NotNull UUID userId) {}
