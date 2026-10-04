package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Role;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.GetUserQueryService;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByIdentityIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByUsernameQuery;
import com.deepprotech.deepproject.iam.queries.ListUsersQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserQueryServiceImpl implements GetUserQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .identityId(rs.getString("identity_id"))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .displayName(rs.getString("display_name"))
            .active(rs.getBoolean("active"))
            .build();

    private static final RowMapper<Role> ROLE_MAPPER = (rs, rowNum) -> Role.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .build();

    @Override
    public User handle(GetUserByIdQuery query) {
        List<User> list = jdbc.query("SELECT * FROM users WHERE id = ?", USER_MAPPER, query.userId());
        if (list.isEmpty()) throw new ResourceNotFoundException("User", query.userId());
        return list.get(0);
    }

    @Override
    public User handle(GetUserByUsernameQuery query) {
        List<User> list = jdbc.query("SELECT * FROM users WHERE username = ?", USER_MAPPER, query.username());
        if (list.isEmpty()) throw new ResourceNotFoundException("User not found: " + query.username());
        return list.get(0);
    }

    @Override
    public User handle(GetUserByIdentityIdQuery query) {
        List<User> list = jdbc.query("SELECT * FROM users WHERE identity_id = ?", USER_MAPPER, query.identityId());
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<User> handle(ListUsersQuery query) {
        return jdbc.query("SELECT * FROM users ORDER BY id", USER_MAPPER);
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        return jdbc.query(
                "SELECT r.* FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                ROLE_MAPPER, userId);
    }
}