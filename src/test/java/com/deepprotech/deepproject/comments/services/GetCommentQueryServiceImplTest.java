package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCommentQueryServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks GetCommentQueryServiceImpl service;

    private static final UUID COMMENT_ID = UUID.fromString("a0000012-0000-0000-0000-000000000001");
    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Test
    void getByIdReturnsComment() {
        Comment comment = Comment.builder().id(COMMENT_ID).content("Comment").build();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        assertThat(service.handle(new GetCommentByIdQuery(COMMENT_ID))).isEqualTo(comment);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(commentRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetCommentByIdQuery(UNKNOWN_ID))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByTaskReturnsComments() {
        Comment comment = Comment.builder().id(COMMENT_ID).taskId(TASK_ID).build();
        when(commentRepository.findByTaskIdOrderByCreatedAtAsc(TASK_ID)).thenReturn(List.of(comment));
        assertThat(service.handle(new ListCommentsByTaskQuery(TASK_ID))).containsExactly(comment);
    }
}
