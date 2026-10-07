package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveTaskTagCommand(@NotNull UUID taskId, @NotBlank String tagName) {}
