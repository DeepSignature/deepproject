package com.deepprotech.deepproject.comments.api;

import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.core.Comment;

import java.util.List;

public interface GetCommentQueryService {
    Comment handle(GetCommentByIdQuery query);
    List<Comment> handle(ListCommentsByTaskQuery query);
}
