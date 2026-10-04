package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.DeleteCommentCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteCommentServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks DeleteCommentServiceImpl service;

    @Test
    void deletesComment() {
        service.handle(new DeleteCommentCommand(1L));
        verify(jdbc).update("DELETE FROM comments WHERE id = ?", 1L);
    }
}