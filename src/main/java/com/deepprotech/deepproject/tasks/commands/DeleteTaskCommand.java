package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteTaskCommand(@NotNull UUID taskId) {}
