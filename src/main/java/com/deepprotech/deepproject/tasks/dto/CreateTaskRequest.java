package com.deepprotech.deepproject.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank @Size(max = 500) String title,
        @Size(max = 5000) String description,
        @NotBlank String priority,
        @NotBlank String taskType
) {}
