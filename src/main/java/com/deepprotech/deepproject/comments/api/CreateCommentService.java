package com.deepprotech.deepproject.comments.api;

import com.deepprotech.deepproject.comments.commands.CreateCommentCommand;
import com.deepprotech.deepproject.core.Comment;

public interface CreateCommentService {
    Comment handle(CreateCommentCommand command);
}
