package com.deepprotech.deepproject.sprints.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetSprintByIdQuery(@NotNull UUID sprintId) {}
