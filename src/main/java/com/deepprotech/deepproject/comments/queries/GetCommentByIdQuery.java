package com.deepprotech.deepproject.comments.queries;

import jakarta.validation.constraints.NotNull;

public record GetCommentByIdQuery(@NotNull Long commentId) {}
