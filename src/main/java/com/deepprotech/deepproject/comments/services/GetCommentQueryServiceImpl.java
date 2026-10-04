package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.GetCommentQueryService;
import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCommentQueryServiceImpl implements GetCommentQueryService {

    private final CommentRepository commentRepository;

    @Override
    public Comment handle(GetCommentByIdQuery query) {
        return commentRepository.findById(query.commentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment", query.commentId()));
    }

    @Override
    public List<Comment> handle(ListCommentsByTaskQuery query) {
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(query.taskId());
    }
}
