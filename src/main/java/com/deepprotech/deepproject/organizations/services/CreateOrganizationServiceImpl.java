package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.api.CreateOrganizationService;
import com.deepprotech.deepproject.organizations.commands.CreateOrganizationCommand;
import com.deepprotech.deepproject.organizations.constants.OrganizationRole;
import com.deepprotech.deepproject.organizations.events.OrganizationCreatedEvent;
import com.deepprotech.deepproject.organizations.repository.OrganizationMemberRepository;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrganizationServiceImpl implements CreateOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Organization handle(CreateOrganizationCommand command) {
        Organization orgEntity = Organization.builder()
                .identifier(command.identifier())
                .name(command.name())
                .description(command.description())
                .build();
        orgEntity = organizationRepository.save(orgEntity);

        OrganizationMember member = OrganizationMember.builder()
                .organizationId(orgEntity.getId())
                .userId(command.createdByUserId())
                .role(OrganizationRole.ORGANIZATION_ADMIN.name())
                .build();
        organizationMemberRepository.save(member);

        eventPublisher.publishEvent(new OrganizationCreatedEvent(orgEntity.getId(), orgEntity.getIdentifier(), orgEntity.getName(), command.createdByUserId(), Instant.now()));
        log.info("organization_created id={} identifier={}", orgEntity.getId(), orgEntity.getIdentifier());
        return orgEntity;
    }
}
