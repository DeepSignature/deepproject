package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.CreateCommentService;
import com.deepprotech.deepproject.comments.commands.CreateCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.core.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCommentServiceImpl implements CreateCommentService {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Comment handle(CreateCommentCommand command) {
        Comment c = Comment.builder()
                .taskId(command.taskId())
                .authorId(command.authorId())
                .content(command.content())
                .build();

        c = commentRepository.save(c);
        log.info("comment_created id={} taskId={}", c.getId(), command.taskId());
        return c;
    }
}
