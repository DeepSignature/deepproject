package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RemoveTaskTagCommand(@NotNull Long taskId, @NotBlank String tagName) {}
