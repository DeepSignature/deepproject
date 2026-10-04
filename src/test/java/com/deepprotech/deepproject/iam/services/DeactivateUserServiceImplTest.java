package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.commands.DeactivateUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeactivateUserServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks DeactivateUserServiceImpl service;

    @Test
    void deactivatesUser() {
        service.handle(new DeactivateUserCommand(1L));
        verify(jdbc).update("UPDATE users SET active = false WHERE id = ?", 1L);
    }
}