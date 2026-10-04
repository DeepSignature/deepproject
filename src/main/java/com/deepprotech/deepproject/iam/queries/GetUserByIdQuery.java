package com.deepprotech.deepproject.iam.queries;

import jakarta.validation.constraints.NotNull;

public record GetUserByIdQuery(@NotNull Long userId) {}
