package com.deepprotech.deepproject.projects.commands;

import jakarta.validation.constraints.NotNull;

public record DeleteProjectCommand(@NotNull Long projectId) {}
