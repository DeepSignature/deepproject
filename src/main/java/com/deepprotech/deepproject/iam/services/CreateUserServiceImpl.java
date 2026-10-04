package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.CreateUserService;
import com.deepprotech.deepproject.iam.commands.CreateUserCommand;
import com.deepprotech.deepproject.iam.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserServiceImpl implements CreateUserService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

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
    public User handle(CreateUserCommand command) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (identity_id, username, email, display_name) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, command.identityId());
            ps.setString(2, command.username());
            ps.setString(3, command.email());
            ps.setString(4, command.displayName());
            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        List<User> list = jdbc.query("SELECT * FROM users WHERE id = ?", USER_MAPPER, id);
        if (list.isEmpty()) throw new ResourceNotFoundException("User", id);
        User user = list.get(0);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getUsername(), user.getEmail(), Instant.now()));
        log.info("user_created id={} username={}", id, command.username());
        return user;
    }
}