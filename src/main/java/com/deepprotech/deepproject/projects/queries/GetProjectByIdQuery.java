package com.deepprotech.deepproject.projects.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetProjectByIdQuery(@NotNull UUID projectId) {}
