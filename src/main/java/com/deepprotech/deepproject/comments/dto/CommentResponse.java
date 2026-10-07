package com.deepprotech.deepproject.comments.dto;

import com.deepprotech.deepproject.core.Comment;

import java.util.UUID;

public record CommentResponse(UUID id, UUID taskId, UUID authorId, String content) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getId(), c.getTaskId(), c.getAuthorId(), c.getContent());
    }
}
