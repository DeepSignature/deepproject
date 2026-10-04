package com.deepprotech.deepproject.comments.queries;

import jakarta.validation.constraints.NotNull;

public record ListCommentsByTaskQuery(@NotNull Long taskId) {}
