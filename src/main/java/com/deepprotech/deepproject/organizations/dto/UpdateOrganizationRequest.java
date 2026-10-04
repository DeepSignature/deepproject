package com.deepprotech.deepproject.organizations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOrganizationRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description
) {}