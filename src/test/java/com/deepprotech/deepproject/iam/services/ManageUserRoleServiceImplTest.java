package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.commands.AssignUserRoleCommand;
import com.deepprotech.deepproject.iam.commands.RemoveUserRoleCommand;
import com.deepprotech.deepproject.iam.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageUserRoleServiceImplTest {

    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID ROLE_ID = UUID.fromString("a0000004-0000-0000-0000-000000000001");

    @Mock RoleRepository roleRepository;
    @InjectMocks ManageUserRoleServiceImpl service;

    @Test
    void assignsRole() {
        service.handle(new AssignUserRoleCommand(USER_ID, ROLE_ID));
        verify(roleRepository).assignRoleToUser(USER_ID, ROLE_ID);
    }

    @Test
    void removesRole() {
        service.handle(new RemoveUserRoleCommand(USER_ID, ROLE_ID));
        verify(roleRepository).removeRoleFromUser(USER_ID, ROLE_ID);
    }
}
