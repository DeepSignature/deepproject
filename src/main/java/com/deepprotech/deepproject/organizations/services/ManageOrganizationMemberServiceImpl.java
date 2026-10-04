package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.organizations.api.ManageOrganizationMemberService;
import com.deepprotech.deepproject.organizations.commands.AddOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.RemoveOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationMemberRoleCommand;
import com.deepprotech.deepproject.organizations.events.OrganizationMemberAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageOrganizationMemberServiceImpl implements ManageOrganizationMemberService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(AddOrganizationMemberCommand command) {
        jdbc.update("INSERT INTO organization_members (organization_id, user_id, role) VALUES (?, ?, ?) ON CONFLICT (organization_id, user_id) DO NOTHING",
                command.organizationId(), command.userId(), command.role());
        eventPublisher.publishEvent(new OrganizationMemberAddedEvent(command.organizationId(), command.userId(), command.role(), Instant.now()));
        log.info("org_member_added org={} user={} role={}", command.organizationId(), command.userId(), command.role());
    }

    @Override
    @Transactional
    public void handle(RemoveOrganizationMemberCommand command) {
        jdbc.update("DELETE FROM organization_members WHERE organization_id = ? AND user_id = ?",
                command.organizationId(), command.userId());
        log.info("org_member_removed org={} user={}", command.organizationId(), command.userId());
    }

    @Override
    @Transactional
    public void handle(UpdateOrganizationMemberRoleCommand command) {
        jdbc.update("UPDATE organization_members SET role = ?, updated_at = ? WHERE organization_id = ? AND user_id = ?",
                command.role(), java.sql.Timestamp.from(Instant.now()), command.organizationId(), command.userId());
        log.info("org_member_role_updated org={} user={} role={}", command.organizationId(), command.userId(), command.role());
    }
}