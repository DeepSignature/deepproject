package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.UpdateCommentService;
import com.deepprotech.deepproject.comments.commands.UpdateCommentCommand;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCommentServiceImpl implements UpdateCommentService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Comment> MAPPER = (rs, rowNum) -> Comment.builder()
            .id(rs.getLong("id"))
            .taskId(rs.getLong("task_id"))
            .authorId(rs.getLong("author_id"))
            .content(rs.getString("content"))
            .build();

    @Override
    @Transactional
    public Comment handle(UpdateCommentCommand command) {
        jdbc.update("UPDATE comments SET content = ?, updated_at = ? WHERE id = ?",
                command.content(), Timestamp.from(Instant.now()), command.commentId());
        log.info("comment_updated id={}", command.commentId());
        List<Comment> list = jdbc.query("SELECT * FROM comments WHERE id = ?", MAPPER, command.commentId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Comment", command.commentId());
        return list.get(0);
    }
}
