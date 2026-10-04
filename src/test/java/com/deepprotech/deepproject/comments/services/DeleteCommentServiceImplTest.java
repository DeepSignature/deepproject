package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.DeleteCommentCommand;
import com.deepprotech.deepproject.comments.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteCommentServiceImplTest {

    @Mock CommentRepository commentRepository;
    @InjectMocks DeleteCommentServiceImpl service;

    @Test
    void deletesComment() {
        service.handle(new DeleteCommentCommand(10L));
        verify(commentRepository).deleteById(10L);
    }
}
