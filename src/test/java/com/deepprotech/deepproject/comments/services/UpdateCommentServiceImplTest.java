package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.UpdateCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCommentServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks UpdateCommentServiceImpl service;

    @Test
    void updatesCommentReturnsIt() {
        UpdateCommentCommand cmd = new UpdateCommentCommand(10L, "Updated content");
        Comment comment = Comment.builder().id(10L).content("Old content").build();
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        Comment result = service.handle(cmd);

        assertThat(result.getContent()).isEqualTo("Updated content");
        verify(commentRepository).save(comment);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateCommentCommand cmd = new UpdateCommentCommand(999L, "Updated content");
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
