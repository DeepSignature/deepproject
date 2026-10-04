package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.api.UpdateOrganizationService;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrganizationServiceImpl implements UpdateOrganizationService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Organization> ORG_MAPPER = (rs, rowNum) -> Organization.builder()
            .id(rs.getLong("id"))
            .identifier(rs.getString("identifier"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .build();

    @Override
    @Transactional
    public Organization handle(UpdateOrganizationCommand command) {
        jdbc.update("UPDATE organizations SET name = ?, description = ?, updated_at = ? WHERE id = ?",
                command.name(), command.description(), Timestamp.from(Instant.now()), command.organizationId());
        log.info("organization_updated id={}", command.organizationId());
        List<Organization> list = jdbc.query("SELECT * FROM organizations WHERE id = ?", ORG_MAPPER, command.organizationId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Organization", command.organizationId());
        return list.get(0);
    }
}