package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.api.DeactivateUserService;
import com.deepprotech.deepproject.iam.commands.DeactivateUserCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivateUserServiceImpl implements DeactivateUserService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(DeactivateUserCommand command) {
        jdbc.update("UPDATE users SET active = false WHERE id = ?", command.userId());
        log.info("user_deactivated id={}", command.userId());
    }
}