package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.UpdateCommentService;
import com.deepprotech.deepproject.comments.commands.UpdateCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCommentServiceImpl implements UpdateCommentService {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Comment handle(UpdateCommentCommand command) {
        Comment c = commentRepository.findById(command.commentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment", command.commentId()));

        c.setContent(command.content());
        Comment updated = commentRepository.save(c);
        log.info("comment_updated id={}", command.commentId());
        return updated;
    }
}
