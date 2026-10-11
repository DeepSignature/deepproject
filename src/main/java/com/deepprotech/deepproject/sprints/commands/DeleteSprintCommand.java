package com.deepprotech.deepproject.sprints.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteSprintCommand(@NotNull UUID sprintId) {}
