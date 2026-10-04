package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.CreateCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.core.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCommentServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks CreateCommentServiceImpl service;

    @Test
    void createsComment() {
        CreateCommentCommand cmd = new CreateCommentCommand(100L, 1L, "Great work!");
        Comment comment = Comment.builder().id(10L).taskId(100L).authorId(1L).content("Great work!").build();
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getContent()).isEqualTo("Great work!");
        verify(commentRepository).save(any(Comment.class));
    }
}
