package com.deepprotech.deepproject.comments.api;

import com.deepprotech.deepproject.comments.commands.DeleteCommentCommand;

public interface DeleteCommentService {
    void handle(DeleteCommentCommand command);
}
