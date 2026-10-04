package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.commands.CreateUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateUserServiceImpl service;

    @Test
    void createsUserAndPublishesEvent() {
        CreateUserCommand cmd = new CreateUserCommand("id-1", "testuser", "test@example.com", "Test User");
        User mockUser = User.builder().id(1L).identityId("id-1").username("testuser").email("test@example.com").displayName("Test User").active(true).build();

        when(jdbc.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(inv -> {
            KeyHolder kh = inv.getArgument(1);
            kh.getKeyList().add(Map.of("id", 1L));
            return 1;
        });
        when(jdbc.query(eq("SELECT * FROM users WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(mockUser));

        User result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenUserNotFoundAfterInsert() {
        CreateUserCommand cmd = new CreateUserCommand("id-1", "testuser", "test@example.com", "Test User");

        when(jdbc.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(inv -> {
            KeyHolder kh = inv.getArgument(1);
            kh.getKeyList().add(Map.of("id", 1L));
            return 1;
        });
        when(jdbc.query(eq("SELECT * FROM users WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}