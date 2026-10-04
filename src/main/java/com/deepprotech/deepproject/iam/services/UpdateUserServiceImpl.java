package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.UpdateUserService;
import com.deepprotech.deepproject.iam.commands.UpdateUserCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserServiceImpl implements UpdateUserService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .identityId(rs.getString("identity_id"))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .displayName(rs.getString("display_name"))
            .active(rs.getBoolean("active"))
            .build();

    @Override
    @Transactional
    public User handle(UpdateUserCommand command) {
        jdbc.update("UPDATE users SET display_name = ?, updated_at = ? WHERE id = ?",
                command.displayName(), Timestamp.from(Instant.now()), command.userId());
        log.info("user_updated id={}", command.userId());
        List<User> list = jdbc.query("SELECT * FROM users WHERE id = ?", USER_MAPPER, command.userId());
        if (list.isEmpty()) throw new ResourceNotFoundException("User", command.userId());
        return list.get(0);
    }
}