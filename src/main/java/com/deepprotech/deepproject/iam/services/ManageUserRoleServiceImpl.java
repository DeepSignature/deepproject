package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.api.ManageUserRoleService;
import com.deepprotech.deepproject.iam.commands.AssignUserRoleCommand;
import com.deepprotech.deepproject.iam.commands.RemoveUserRoleCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageUserRoleServiceImpl implements ManageUserRoleService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(AssignUserRoleCommand command) {
        jdbc.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                command.userId(), command.roleId());
        log.info("role_assigned userId={} roleId={}", command.userId(), command.roleId());
    }

    @Override
    @Transactional
    public void handle(RemoveUserRoleCommand command) {
        jdbc.update("DELETE FROM user_roles WHERE user_id = ? AND role_id = ?",
                command.userId(), command.roleId());
        log.info("role_removed userId={} roleId={}", command.userId(), command.roleId());
    }
}
