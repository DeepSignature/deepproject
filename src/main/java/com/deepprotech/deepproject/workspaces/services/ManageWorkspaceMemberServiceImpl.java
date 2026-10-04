package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.api.ManageWorkspaceMemberService;
import com.deepprotech.deepproject.workspaces.commands.AddWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.RemoveWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceMemberRoleCommand;
import com.deepprotech.deepproject.workspaces.events.MemberAddedEvent;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageWorkspaceMemberServiceImpl implements ManageWorkspaceMemberService {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(AddWorkspaceMemberCommand command) {
        if (workspaceMemberRepository.findByWorkspaceIdAndUserId(command.workspaceId(), command.userId()).isEmpty()) {
            WorkspaceMember member = WorkspaceMember.builder()
                    .workspaceId(command.workspaceId())
                    .userId(command.userId())
                    .role(command.role())
                    .build();
            workspaceMemberRepository.save(member);
        }
        eventPublisher.publishEvent(new MemberAddedEvent(command.workspaceId(), command.userId(), command.role(), Instant.now()));
        log.info("workspace_member_added workspaceId={} userId={} role={}", command.workspaceId(), command.userId(), command.role());
    }

    @Override
    @Transactional
    public void handle(RemoveWorkspaceMemberCommand command) {
        workspaceMemberRepository.deleteByWorkspaceIdAndUserId(command.workspaceId(), command.userId());
        log.info("workspace_member_removed workspaceId={} userId={}", command.workspaceId(), command.userId());
    }

    @Override
    @Transactional
    public void handle(UpdateWorkspaceMemberRoleCommand command) {
        workspaceMemberRepository.findByWorkspaceIdAndUserId(command.workspaceId(), command.userId())
                .ifPresent(member -> {
                    member.setRole(command.role());
                    workspaceMemberRepository.save(member);
                });
        log.info("workspace_member_role_updated workspaceId={} userId={} role={}", command.workspaceId(), command.userId(), command.role());
    }
}
