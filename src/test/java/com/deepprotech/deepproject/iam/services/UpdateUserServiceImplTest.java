package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.commands.UpdateUserCommand;
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
class UpdateUserServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks UpdateUserServiceImpl service;

    @Test
    void updatesUserAndReturnsIt() {
        UpdateUserCommand cmd = new UpdateUserCommand(1L, "Updated Name");
        User mockUser = User.builder().id(1L).identityId("id-1").username("testuser").email("test@example.com").displayName("Updated Name").active(true).build();

        when(jdbc.query(eq("SELECT * FROM users WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(mockUser));

        User result = service.handle(cmd);

        assertThat(result.getDisplayName()).isEqualTo("Updated Name");
        verify(jdbc).update(eq("UPDATE users SET display_name = ?, updated_at = ? WHERE id = ?"), any(), any(), eq(1L));
    }

    @Test
    void throwsWhenUserNotFound() {
        UpdateUserCommand cmd = new UpdateUserCommand(999L, "Updated Name");
        when(jdbc.query(eq("SELECT * FROM users WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}