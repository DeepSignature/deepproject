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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCommentServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks UpdateCommentServiceImpl service;

    private static final UUID COMMENT_ID = UUID.fromString("a0000012-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Test
    void updatesCommentReturnsIt() {
        UpdateCommentCommand cmd = new UpdateCommentCommand(COMMENT_ID, "Updated content");
        Comment comment = Comment.builder().id(COMMENT_ID).content("Old content").build();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        Comment result = service.handle(cmd);

        assertThat(result.getContent()).isEqualTo("Updated content");
        verify(commentRepository).save(comment);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateCommentCommand cmd = new UpdateCommentCommand(UNKNOWN_ID, "Updated content");
        when(commentRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
