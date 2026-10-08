package com.deepprotech.deepproject.comments.api;

import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Comment;

public interface GetCommentQueryService {
    Comment handle(GetCommentByIdQuery query);
    CursorPage<Comment> handle(ListCommentsByTaskQuery query);
}
