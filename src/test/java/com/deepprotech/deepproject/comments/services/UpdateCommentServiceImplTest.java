package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.UpdateCommentCommand;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCommentServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks UpdateCommentServiceImpl service;

    @Test
    void updatesCommentReturnsIt() {
        UpdateCommentCommand cmd = new UpdateCommentCommand(1L, "Updated content");
        Comment comment = Comment.builder().id(1L).taskId(1L).authorId(2L).content("Updated content").build();
        when(jdbc.query(eq("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(comment));

        Comment result = service.handle(cmd);

        assertThat(result.getContent()).isEqualTo("Updated content");
        verify(jdbc).update(eq("UPDATE comments SET content = ?, updated_at = ? WHERE id = ?"),
                eq("Updated content"), any(), eq(1L));
    }

    @Test
    void throwsWhenNotFound() {
        UpdateCommentCommand cmd = new UpdateCommentCommand(999L, "Updated content");
        when(jdbc.query(eq("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}