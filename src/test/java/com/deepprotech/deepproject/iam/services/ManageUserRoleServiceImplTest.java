package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.commands.AssignUserRoleCommand;
import com.deepprotech.deepproject.iam.commands.RemoveUserRoleCommand;
import com.deepprotech.deepproject.iam.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageUserRoleServiceImplTest {

    @Mock RoleRepository roleRepository;
    @InjectMocks ManageUserRoleServiceImpl service;

    @Test
    void assignsRole() {
        service.handle(new AssignUserRoleCommand(1L, 2L));
        verify(roleRepository).assignRoleToUser(1L, 2L);
    }

    @Test
    void removesRole() {
        service.handle(new RemoveUserRoleCommand(1L, 2L));
        verify(roleRepository).removeRoleFromUser(1L, 2L);
    }
}
