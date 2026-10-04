package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.api.GetOrganizationQueryService;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdentifierQuery;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;
import com.deepprotech.deepproject.organizations.repository.OrganizationMemberRepository;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetOrganizationQueryServiceImpl implements GetOrganizationQueryService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    public Organization handle(GetOrganizationByIdQuery query) {
        return organizationRepository.findById(query.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", query.organizationId()));
    }

    @Override
    public Organization handle(GetOrganizationByIdentifierQuery query) {
        return organizationRepository.findByIdentifier(query.identifier())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + query.identifier()));
    }

    @Override
    public List<Organization> handle(ListUserOrganizationsQuery query) {
        return organizationRepository.findOrganizationsByUserId(query.userId());
    }

    @Override
    public List<OrganizationMember> handle(ListOrganizationMembersQuery query) {
        return organizationMemberRepository.findByOrganizationId(query.organizationId());
    }
}
