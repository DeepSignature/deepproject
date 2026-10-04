package com.deepprotech.deepproject.iam.queries;

import jakarta.validation.constraints.NotBlank;

public record GetUserByIdentityIdQuery(@NotBlank String identityId) {}