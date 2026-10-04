package com.deepprotech.deepproject.comments.services;

import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCommentQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetCommentQueryServiceImpl service;

    private final Comment comment = Comment.builder().id(1L).taskId(1L).authorId(2L).content("Nice!").build();

    @Test
    void getByIdReturnsComment() {
        when(jdbc.query(eq("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(comment));
        assertThat(service.handle(new GetCommentByIdQuery(1L)).getId()).isEqualTo(1L);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetCommentByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByTaskReturnsComments() {
        when(jdbc.query(eq("SELECT * FROM comments WHERE task_id = ? ORDER BY id"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(comment));
        assertThat(service.handle(new ListCommentsByTaskQuery(1L))).hasSize(1);
    }
}