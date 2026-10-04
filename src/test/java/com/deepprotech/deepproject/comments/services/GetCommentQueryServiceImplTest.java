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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCommentQueryServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks GetCommentQueryServiceImpl service;

    @Test
    void getByIdReturnsComment() {
        Comment comment = Comment.builder().id(10L).content("Comment").build();
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        assertThat(service.handle(new GetCommentByIdQuery(10L))).isEqualTo(comment);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetCommentByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByTaskReturnsComments() {
        Comment comment = Comment.builder().id(10L).taskId(100L).build();
        when(commentRepository.findByTaskIdOrderByCreatedAtAsc(100L)).thenReturn(List.of(comment));
        assertThat(service.handle(new ListCommentsByTaskQuery(100L))).containsExactly(comment);
    }
}
