package com.deepprotech.deepproject.comments.dto;

import com.deepprotech.deepproject.core.Comment;

public record CommentResponse(Long id, Long taskId, Long authorId, String content) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getId(), c.getTaskId(), c.getAuthorId(), c.getContent());
    }
}
