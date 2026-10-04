package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.commands.AssignUserRoleCommand;
import com.deepprotech.deepproject.iam.commands.RemoveUserRoleCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageUserRoleServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks ManageUserRoleServiceImpl service;

    @Test
    void assignsRole() {
        service.handle(new AssignUserRoleCommand(1L, 2L));
        verify(jdbc).update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?) ON CONFLICT DO NOTHING", 1L, 2L);
    }

    @Test
    void removesRole() {
        service.handle(new RemoveUserRoleCommand(1L, 2L));
        verify(jdbc).update("DELETE FROM user_roles WHERE user_id = ? AND role_id = ?", 1L, 2L);
    }
}