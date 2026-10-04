package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.workspaces.commands.AddWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.RemoveWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceMemberRoleCommand;
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
class ManageWorkspaceMemberServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ManageWorkspaceMemberServiceImpl service;

    @Test
    void addsMember() {
        service.handle(new AddWorkspaceMemberCommand(1L, 2L, "MEMBER"));
        verify(jdbc).update(eq("INSERT INTO workspace_members (workspace_id, user_id, role) VALUES (?, ?, ?) ON CONFLICT DO NOTHING"),
                eq(1L), eq(2L), eq("MEMBER"));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void removesMember() {
        service.handle(new RemoveWorkspaceMemberCommand(1L, 2L));
        verify(jdbc).update(eq("DELETE FROM workspace_members WHERE workspace_id = ? AND user_id = ?"), eq(1L), eq(2L));
    }

    @Test
    void updatesMemberRole() {
        service.handle(new UpdateWorkspaceMemberRoleCommand(1L, 2L, "ADMIN"));
        verify(jdbc).update(eq("UPDATE workspace_members SET role = ?, updated_at = ? WHERE workspace_id = ? AND user_id = ?"),
                eq("ADMIN"), any(), eq(1L), eq(2L));
    }
}
