package com.deepprotech.deepproject.comments.api;

import com.deepprotech.deepproject.comments.commands.UpdateCommentCommand;
import com.deepprotech.deepproject.core.Comment;

public interface UpdateCommentService {
    Comment handle(UpdateCommentCommand command);
}
