package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.commands.AddWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.RemoveWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceMemberRoleCommand;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageWorkspaceMemberServiceImplTest {

    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");
    private static final UUID USER_ID_2 = UUID.fromString("a0000002-0000-0000-0000-000000000002");

    @Mock WorkspaceMemberRepository workspaceMemberRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ManageWorkspaceMemberServiceImpl service;

    @Test
    void addsMember() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(WS_ID, USER_ID_2)).thenReturn(Optional.empty());
        service.handle(new AddWorkspaceMemberCommand(WS_ID, USER_ID_2, "MEMBER"));
        verify(workspaceMemberRepository).save(any(WorkspaceMember.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void removesMember() {
        service.handle(new RemoveWorkspaceMemberCommand(WS_ID, USER_ID_2));
        verify(workspaceMemberRepository).deleteByWorkspaceIdAndUserId(WS_ID, USER_ID_2);
    }

    @Test
    void updatesMemberRole() {
        WorkspaceMember member = WorkspaceMember.builder().workspaceId(WS_ID).userId(USER_ID_2).role("MEMBER").build();
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(WS_ID, USER_ID_2)).thenReturn(Optional.of(member));
        service.handle(new UpdateWorkspaceMemberRoleCommand(WS_ID, USER_ID_2, "ADMIN"));
        verify(workspaceMemberRepository).save(member);
    }
}
