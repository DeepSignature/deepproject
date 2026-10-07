package com.deepprotech.deepproject.projects.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteProjectCommand(@NotNull UUID projectId) {}
