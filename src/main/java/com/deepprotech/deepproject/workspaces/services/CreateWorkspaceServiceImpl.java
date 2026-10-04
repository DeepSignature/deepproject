package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.api.CreateWorkspaceService;
import com.deepprotech.deepproject.workspaces.commands.CreateWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.events.WorkspaceCreatedEvent;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceMemberRepository;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWorkspaceServiceImpl implements CreateWorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Workspace handle(CreateWorkspaceCommand command) {
        Workspace ws = Workspace.builder()
                .name(command.name())
                .slug(command.slug())
                .description(command.description())
                .ownerId(command.ownerId())
                .organizationId(command.organizationId())
                .build();

        ws = workspaceRepository.save(ws);

        WorkspaceMember member = WorkspaceMember.builder()
                .workspaceId(ws.getId())
                .userId(command.ownerId())
                .role("OWNER")
                .build();
        workspaceMemberRepository.save(member);

        eventPublisher.publishEvent(new WorkspaceCreatedEvent(ws.getId(), ws.getName(), ws.getOwnerId(), Instant.now()));
        log.info("workspace_created id={} name={}", ws.getId(), command.name());
        return ws;
    }
}
