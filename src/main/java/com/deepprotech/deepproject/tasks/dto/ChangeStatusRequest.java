package com.deepprotech.deepproject.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeStatusRequest(@NotBlank @Size(max = 500) String status){}
