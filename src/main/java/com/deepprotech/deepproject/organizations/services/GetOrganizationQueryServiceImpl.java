package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.api.GetOrganizationQueryService;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdentifierQuery;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetOrganizationQueryServiceImpl implements GetOrganizationQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Organization> ORG_MAPPER = (rs, rowNum) -> Organization.builder()
            .id(rs.getLong("id"))
            .identifier(rs.getString("identifier"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .build();

    private static final RowMapper<OrganizationMember> OM_MAPPER = (rs, rowNum) -> OrganizationMember.builder()
            .id(rs.getLong("id"))
            .organizationId(rs.getLong("organization_id"))
            .userId(rs.getLong("user_id"))
            .role(rs.getString("role"))
            .build();

    @Override
    public Organization handle(GetOrganizationByIdQuery query) {
        List<Organization> list = jdbc.query("SELECT * FROM organizations WHERE id = ?", ORG_MAPPER, query.organizationId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Organization", query.organizationId());
        return list.get(0);
    }

    @Override
    public Organization handle(GetOrganizationByIdentifierQuery query) {
        List<Organization> list = jdbc.query("SELECT * FROM organizations WHERE identifier = ?", ORG_MAPPER, query.identifier());
        if (list.isEmpty()) throw new ResourceNotFoundException("Organization not found: " + query.identifier());
        return list.get(0);
    }

    @Override
    public List<Organization> handle(ListUserOrganizationsQuery query) {
        return jdbc.query("SELECT o.* FROM organizations o INNER JOIN organization_members om ON o.id = om.organization_id WHERE om.user_id = ?",
                ORG_MAPPER, query.userId());
    }

    @Override
    public List<OrganizationMember> handle(ListOrganizationMembersQuery query) {
        return jdbc.query("SELECT * FROM organization_members WHERE organization_id = ?", OM_MAPPER, query.organizationId());
    }
}