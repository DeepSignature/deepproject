package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.api.ManageOrganizationMemberService;
import com.deepprotech.deepproject.organizations.commands.AddOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.RemoveOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationMemberRoleCommand;
import com.deepprotech.deepproject.organizations.events.OrganizationMemberAddedEvent;
import com.deepprotech.deepproject.organizations.repository.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageOrganizationMemberServiceImpl implements ManageOrganizationMemberService {

    private final OrganizationMemberRepository organizationMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(AddOrganizationMemberCommand command) {
        if (organizationMemberRepository.findByOrganizationIdAndUserId(command.organizationId(), command.userId()).isEmpty()) {
            OrganizationMember member = OrganizationMember.builder()
                    .organizationId(command.organizationId())
                    .userId(command.userId())
                    .role(command.role())
                    .build();
            organizationMemberRepository.save(member);
        }
        eventPublisher.publishEvent(new OrganizationMemberAddedEvent(command.organizationId(), command.userId(), command.role(), Instant.now()));
        log.info("org_member_added orgId={} userId={} role={}", command.organizationId(), command.userId(), command.role());
    }

    @Override
    @Transactional
    public void handle(RemoveOrganizationMemberCommand command) {
        organizationMemberRepository.deleteByOrganizationIdAndUserId(command.organizationId(), command.userId());
        log.info("org_member_removed orgId={} userId={}", command.organizationId(), command.userId());
    }

    @Override
    @Transactional
    public void handle(UpdateOrganizationMemberRoleCommand command) {
        organizationMemberRepository.findByOrganizationIdAndUserId(command.organizationId(), command.userId())
                .ifPresent(member -> {
                    member.setRole(command.role());
                    organizationMemberRepository.save(member);
                });
        log.info("org_member_role_updated orgId={} userId={} role={}", command.organizationId(), command.userId(), command.role());
    }
}
