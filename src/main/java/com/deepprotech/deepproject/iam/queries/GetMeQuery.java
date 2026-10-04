package com.deepprotech.deepproject.iam.queries;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record GetMeQuery(@NotBlank String identityId, @NotBlank List<String> tokenRoles) {}