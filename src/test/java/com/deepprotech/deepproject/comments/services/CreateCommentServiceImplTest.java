package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.CreateCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import com.deepprotech.deepproject.core.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCommentServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks CreateCommentServiceImpl service;

    private static final UUID COMMENT_ID = UUID.fromString("a0000012-0000-0000-0000-000000000001");
    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Test
    void createsComment() {
        CreateCommentCommand cmd = new CreateCommentCommand(TASK_ID, USER_ID, "Great work!");
        Comment comment = Comment.builder().id(COMMENT_ID).taskId(TASK_ID).authorId(USER_ID).content("Great work!").build();
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(COMMENT_ID);
        assertThat(result.getContent()).isEqualTo("Great work!");
        verify(commentRepository).save(any(Comment.class));
    }
}
