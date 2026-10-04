package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.workspaces.api.ManageWorkspaceMemberService;
import com.deepprotech.deepproject.workspaces.commands.AddWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.RemoveWorkspaceMemberCommand;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceMemberRoleCommand;
import com.deepprotech.deepproject.workspaces.events.MemberAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageWorkspaceMemberServiceImpl implements ManageWorkspaceMemberService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(AddWorkspaceMemberCommand command) {
        jdbc.update("INSERT INTO workspace_members (workspace_id, user_id, role) VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
                command.workspaceId(), command.userId(), command.role());
        eventPublisher.publishEvent(new MemberAddedEvent(command.workspaceId(), command.userId(), command.role(), Instant.now()));
        log.info("workspace_member_added workspaceId={} userId={} role={}", command.workspaceId(), command.userId(), command.role());
    }

    @Override
    @Transactional
    public void handle(RemoveWorkspaceMemberCommand command) {
        jdbc.update("DELETE FROM workspace_members WHERE workspace_id = ? AND user_id = ?",
                command.workspaceId(), command.userId());
        log.info("workspace_member_removed workspaceId={} userId={}", command.workspaceId(), command.userId());
    }

    @Override
    @Transactional
    public void handle(UpdateWorkspaceMemberRoleCommand command) {
        jdbc.update("UPDATE workspace_members SET role = ?, updated_at = ? WHERE workspace_id = ? AND user_id = ?",
                command.role(), Timestamp.from(Instant.now()), command.workspaceId(), command.userId());
        log.info("workspace_member_role_updated workspaceId={} userId={} role={}", command.workspaceId(), command.userId(), command.role());
    }
}
