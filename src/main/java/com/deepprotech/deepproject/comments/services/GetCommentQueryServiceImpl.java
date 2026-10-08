package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.api.GetCommentQueryService;
import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
    public CursorPage<Comment> handle(ListCommentsByTaskQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        List<Comment> comments = key == null
                ? commentRepository.findByTaskId(query.taskId(), pageable)
                : commentRepository.findByTaskIdAfter(query.taskId(), key.createdAt(), key.id(), pageable);
        return CursorPages.build(comments, query.limit(), Comment::getCreatedAt, Comment::getId);
    }
}
