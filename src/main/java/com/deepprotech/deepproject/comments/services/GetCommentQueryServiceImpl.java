package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.GetCommentQueryService;
import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCommentQueryServiceImpl implements GetCommentQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Comment> MAPPER = (rs, rowNum) -> Comment.builder()
            .id(rs.getLong("id"))
            .taskId(rs.getLong("task_id"))
            .authorId(rs.getLong("author_id"))
            .content(rs.getString("content"))
            .build();

    @Override
    public Comment handle(GetCommentByIdQuery query) {
        List<Comment> list = jdbc.query("SELECT * FROM comments WHERE id = ?", MAPPER, query.commentId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Comment", query.commentId());
        return list.get(0);
    }

    @Override
    public List<Comment> handle(ListCommentsByTaskQuery query) {
        return jdbc.query("SELECT * FROM comments WHERE task_id = ? ORDER BY id", MAPPER, query.taskId());
    }
}
