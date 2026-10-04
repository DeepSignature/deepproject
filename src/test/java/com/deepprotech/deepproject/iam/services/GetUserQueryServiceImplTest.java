package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Role;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByIdentityIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByUsernameQuery;
import com.deepprotech.deepproject.iam.queries.ListUsersQuery;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetUserQueryServiceImpl service;

    private final User user = User.builder().id(1L).identityId("id-1").username("testuser").email("test@test.com").displayName("Test").active(true).build();
    private final Role role = Role.builder().id(1L).name("SYSTEM_ADMIN").description("Admin").build();

    @Test
    void getByIdReturnsUser() {
        when(jdbc.query(eq("SELECT * FROM users WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(user));
        User result = service.handle(new GetUserByIdQuery(1L));
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM users WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetUserByIdQuery(999L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByUsernameReturnsUser() {
        when(jdbc.query(eq("SELECT * FROM users WHERE username = ?"), any(RowMapper.class), eq("testuser"))).thenReturn(List.of(user));
        User result = service.handle(new GetUserByUsernameQuery("testuser"));
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void getByUsernameThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM users WHERE username = ?"), any(RowMapper.class), eq("unknown"))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetUserByUsernameQuery("unknown")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByIdentityIdReturnsUser() {
        when(jdbc.query(eq("SELECT * FROM users WHERE identity_id = ?"), any(RowMapper.class), eq("id-1"))).thenReturn(List.of(user));
        User result = service.handle(new GetUserByIdentityIdQuery("id-1"));
        assertThat(result.getIdentityId()).isEqualTo("id-1");
    }

    @Test
    void getByIdentityIdReturnsNullWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM users WHERE identity_id = ?"), any(RowMapper.class), eq("unknown"))).thenReturn(Collections.emptyList());
        User result = service.handle(new GetUserByIdentityIdQuery("unknown"));
        assertThat(result).isNull();
    }

    @Test
    void listUsersReturnsAll() {
        when(jdbc.query(eq("SELECT * FROM users ORDER BY id"), any(RowMapper.class))).thenReturn(List.of(user));
        List<User> result = service.handle(new ListUsersQuery());
        assertThat(result).hasSize(1);
    }

    @Test
    void getUserRolesReturnsRoles() {
        when(jdbc.query(contains("INNER JOIN user_roles"), any(RowMapper.class), eq(1L))).thenReturn(List.of(role));
        List<Role> result = service.getUserRoles(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("SYSTEM_ADMIN");
    }
}