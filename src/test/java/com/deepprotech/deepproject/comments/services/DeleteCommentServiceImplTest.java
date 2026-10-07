package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.DeleteCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteCommentServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks DeleteCommentServiceImpl service;

    private static final UUID COMMENT_ID = UUID.fromString("a0000012-0000-0000-0000-000000000001");

    @Test
    void deletesComment() {
        service.handle(new DeleteCommentCommand(COMMENT_ID));
        verify(commentRepository).deleteById(COMMENT_ID);
    }
}
