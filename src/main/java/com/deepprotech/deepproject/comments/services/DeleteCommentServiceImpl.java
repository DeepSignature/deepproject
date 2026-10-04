package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.DeleteCommentService;
import com.deepprotech.deepproject.comments.commands.DeleteCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteCommentServiceImpl implements DeleteCommentService {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void handle(DeleteCommentCommand command) {
        commentRepository.deleteById(command.commentId());
        log.info("comment_deleted id={}", command.commentId());
    }
}
