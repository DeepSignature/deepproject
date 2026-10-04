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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageWorkspaceMemberServiceImplTest {

    @Mock WorkspaceMemberRepository workspaceMemberRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ManageWorkspaceMemberServiceImpl service;

    @Test
    void addsMember() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 2L)).thenReturn(Optional.empty());
        service.handle(new AddWorkspaceMemberCommand(1L, 2L, "MEMBER"));
        verify(workspaceMemberRepository).save(any(WorkspaceMember.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void removesMember() {
        service.handle(new RemoveWorkspaceMemberCommand(1L, 2L));
        verify(workspaceMemberRepository).deleteByWorkspaceIdAndUserId(1L, 2L);
    }

    @Test
    void updatesMemberRole() {
        WorkspaceMember member = WorkspaceMember.builder().workspaceId(1L).userId(2L).role("MEMBER").build();
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 2L)).thenReturn(Optional.of(member));
        service.handle(new UpdateWorkspaceMemberRoleCommand(1L, 2L, "ADMIN"));
        verify(workspaceMemberRepository).save(member);
    }
}
