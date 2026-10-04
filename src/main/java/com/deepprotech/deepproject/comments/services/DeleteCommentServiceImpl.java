package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.DeleteCommentService;
import com.deepprotech.deepproject.comments.commands.DeleteCommentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteCommentServiceImpl implements DeleteCommentService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(DeleteCommentCommand command) {
        jdbc.update("DELETE FROM comments WHERE id = ?", command.commentId());
        log.info("comment_deleted id={}", command.commentId());
    }
}
