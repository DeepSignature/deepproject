package com.deepprotech.deepproject.organizations.api;

import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdentifierQuery;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;

import java.util.List;

public interface GetOrganizationQueryService {
    Organization handle(GetOrganizationByIdQuery query);
    Organization handle(GetOrganizationByIdentifierQuery query);
    List<Organization> handle(ListUserOrganizationsQuery query);
    List<OrganizationMember> handle(ListOrganizationMembersQuery query);
}