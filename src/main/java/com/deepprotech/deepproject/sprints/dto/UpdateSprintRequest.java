package com.deepprotech.deepproject.sprints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateSprintRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description,
        Instant startDate,
        Instant endDate
) {}
