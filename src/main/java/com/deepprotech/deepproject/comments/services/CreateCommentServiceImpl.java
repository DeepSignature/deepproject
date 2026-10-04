package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.CreateCommentService;
import com.deepprotech.deepproject.comments.commands.CreateCommentCommand;
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
public class CreateCommentServiceImpl implements CreateCommentService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Comment> MAPPER = (rs, rowNum) -> Comment.builder()
            .id(rs.getLong("id"))
            .taskId(rs.getLong("task_id"))
            .authorId(rs.getLong("author_id"))
            .content(rs.getString("content"))
            .build();

    @Override
    @Transactional
    public Comment handle(CreateCommentCommand command) {
        jdbc.update("INSERT INTO comments (task_id, author_id, content) VALUES (?, ?, ?)",
                command.taskId(), command.authorId(), command.content());
        Long id = jdbc.queryForObject("SELECT LASTVAL()", Long.class);
        log.info("comment_created id={} taskId={}", id, command.taskId());
        List<Comment> list = jdbc.query("SELECT * FROM comments WHERE id = ?", MAPPER, id);
        if (list.isEmpty()) throw new ResourceNotFoundException("Comment", id);
        return list.get(0);
    }
}
