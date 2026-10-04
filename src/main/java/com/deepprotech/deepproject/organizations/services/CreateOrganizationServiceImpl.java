package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.api.CreateOrganizationService;
import com.deepprotech.deepproject.organizations.commands.CreateOrganizationCommand;
import com.deepprotech.deepproject.organizations.events.OrganizationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrganizationServiceImpl implements CreateOrganizationService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    private static final RowMapper<Organization> ORG_MAPPER = (rs, rowNum) -> Organization.builder()
            .id(rs.getLong("id"))
            .identifier(rs.getString("identifier"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .build();

    @Override
    @Transactional
    public Organization handle(CreateOrganizationCommand command) {
        jdbc.update("INSERT INTO organizations (identifier, name, description) VALUES (?, ?, ?)",
                command.identifier(), command.name(), command.description());

        List<Organization> list = jdbc.query("SELECT * FROM organizations WHERE identifier = ?", ORG_MAPPER, command.identifier());
        if (list.isEmpty()) throw new ResourceNotFoundException("Organization not found: " + command.identifier());
        Organization org = list.get(0);

        jdbc.update("INSERT INTO organization_members (organization_id, user_id, role) VALUES (?, ?, ?)",
                org.getId(), command.createdByUserId(), "ORGANIZATION_ADMIN");

        eventPublisher.publishEvent(new OrganizationCreatedEvent(org.getId(), org.getIdentifier(), org.getName(), command.createdByUserId(), Instant.now()));
        log.info("organization_created id={} identifier={}", org.getId(), command.identifier());
        return org;
    }
}