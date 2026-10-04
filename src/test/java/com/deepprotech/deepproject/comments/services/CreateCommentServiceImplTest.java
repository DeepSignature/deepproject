package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.commands.CreateCommentCommand;
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
class CreateCommentServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks CreateCommentServiceImpl service;

    @Test
    void createsComment() {
        CreateCommentCommand cmd = new CreateCommentCommand(1L, 2L, "Nice work!");
        Comment comment = Comment.builder().id(10L).taskId(1L).authorId(2L).content("Nice work!").build();

        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(10L);
        when(jdbc.query(eq("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(10L))).thenReturn(List.of(comment));

        Comment result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getContent()).isEqualTo("Nice work!");
        verify(jdbc).update(eq("INSERT INTO comments (task_id, author_id, content) VALUES (?, ?, ?)"),
                eq(1L), eq(2L), eq("Nice work!"));
    }

    @Test
    void throwsWhenCommentNotFoundAfterInsert() {
        CreateCommentCommand cmd = new CreateCommentCommand(1L, 2L, "Nice work!");
        when(jdbc.queryForObject(eq("SELECT LASTVAL()"), eq(Long.class))).thenReturn(10L);
        when(jdbc.query(eq("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(10L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}