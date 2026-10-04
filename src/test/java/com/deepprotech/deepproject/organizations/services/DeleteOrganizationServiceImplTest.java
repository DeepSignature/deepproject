package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.organizations.commands.DeleteOrganizationCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteOrganizationServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks DeleteOrganizationServiceImpl service;

    @Test
    void deletesOrg() {
        service.handle(new DeleteOrganizationCommand(1L));
        verify(jdbc).update("DELETE FROM organizations WHERE id = ?", 1L);
    }
}
