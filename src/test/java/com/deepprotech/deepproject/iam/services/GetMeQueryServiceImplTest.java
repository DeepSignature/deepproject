package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.dto.UserProfileResponse;
import com.deepprotech.deepproject.iam.queries.GetMeQuery;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMeQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetMeQueryServiceImpl service;

    private final User user = User.builder().id(1L).identityId("id-1").username("testuser").email("test@test.com").displayName("Test").active(true).build();

    @Test
    void returnsProfileForExistingUser() {
        when(jdbc.query(eq("SELECT * FROM users WHERE identity_id = ?"), any(RowMapper.class), eq("id-1"))).thenReturn(List.of(user));
        when(jdbc.query(contains("FROM organizations o INNER JOIN organization_members"), any(RowMapper.class), eq(1L))).thenReturn(Collections.emptyList());

        UserProfileResponse result = service.handle(new GetMeQuery("id-1", List.of("CONTRIBUTOR")));

        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.userId()).isEqualTo(1L);
    }

    @Test
    void provisionsNewUserWhenNotFound() {
        User provisioned = User.builder().id(2L).identityId("new-id-abc").username("kc_new-id-a").email("new-id-abc@placeholder").displayName("kc_new-id-a").active(true).build();
        when(jdbc.query(eq("SELECT * FROM users WHERE identity_id = ?"), any(RowMapper.class), eq("new-id-abc")))
                .thenReturn(Collections.emptyList())
                .thenReturn(List.of(provisioned));
        when(jdbc.query(contains("FROM organizations o INNER JOIN organization_members"), any(RowMapper.class), eq(2L))).thenReturn(Collections.emptyList());

        UserProfileResponse result = service.handle(new GetMeQuery("new-id-abc", List.of("CONTRIBUTOR")));

        assertThat(result.userId()).isEqualTo(2L);
        assertThat(result.username()).isEqualTo("kc_new-id-a");
    }
}