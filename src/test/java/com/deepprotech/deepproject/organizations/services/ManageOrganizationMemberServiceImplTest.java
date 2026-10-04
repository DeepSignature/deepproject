package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.organizations.commands.AddOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.RemoveOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationMemberRoleCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageOrganizationMemberServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ManageOrganizationMemberServiceImpl service;

    @Test
    void addsMember() {
        service.handle(new AddOrganizationMemberCommand(1L, 2L, "ORGANIZATION_MEMBER"));
        verify(jdbc).update(eq("INSERT INTO organization_members (organization_id, user_id, role) VALUES (?, ?, ?) ON CONFLICT (organization_id, user_id) DO NOTHING"),
                eq(1L), eq(2L), eq("ORGANIZATION_MEMBER"));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void removesMember() {
        service.handle(new RemoveOrganizationMemberCommand(1L, 2L));
        verify(jdbc).update(eq("DELETE FROM organization_members WHERE organization_id = ? AND user_id = ?"), eq(1L), eq(2L));
    }

    @Test
    void updatesMemberRole() {
        service.handle(new UpdateOrganizationMemberRoleCommand(1L, 2L, "ORGANIZATION_ADMIN"));
        verify(jdbc).update(eq("UPDATE organization_members SET role = ?, updated_at = ? WHERE organization_id = ? AND user_id = ?"),
                eq("ORGANIZATION_ADMIN"), any(), eq(1L), eq(2L));
    }
}
