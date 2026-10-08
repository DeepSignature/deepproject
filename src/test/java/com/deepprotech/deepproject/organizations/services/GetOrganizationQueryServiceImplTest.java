package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdentifierQuery;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;
import com.deepprotech.deepproject.organizations.queries.PageOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.repository.OrganizationMemberRepository;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrganizationQueryServiceImplTest {

    private static final UUID ORG_ID = UUID.fromString("a0000003-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("a0000005-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock OrganizationRepository organizationRepository;
    @Mock OrganizationMemberRepository organizationMemberRepository;
    @InjectMocks GetOrganizationQueryServiceImpl service;

    @Test
    void getByIdReturnsOrg() {
        Organization org = Organization.builder().id(ORG_ID).name("Acme").build();
        when(organizationRepository.findById(ORG_ID)).thenReturn(Optional.of(org));
        assertThat(service.handle(new GetOrganizationByIdQuery(ORG_ID))).isEqualTo(org);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(organizationRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetOrganizationByIdQuery(UNKNOWN_ID))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByIdentifierReturnsOrg() {
        Organization org = Organization.builder().id(ORG_ID).identifier("acme").build();
        when(organizationRepository.findByIdentifier("acme")).thenReturn(Optional.of(org));
        assertThat(service.handle(new GetOrganizationByIdentifierQuery("acme"))).isEqualTo(org);
    }

    @Test
    void getByIdentifierThrowsWhenNotFound() {
        when(organizationRepository.findByIdentifier("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetOrganizationByIdentifierQuery("unknown"))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listUserOrganizationsReturnsOrgs() {
        Organization org = Organization.builder().id(ORG_ID).build();
        when(organizationRepository.findOrganizationsByUserId(USER_ID)).thenReturn(List.of(org));
        assertThat(service.handle(new ListUserOrganizationsQuery(USER_ID))).containsExactly(org);
    }

    @Test
    void listOrganizationMembersReturnsMembers() {
        OrganizationMember member = OrganizationMember.builder().id(MEMBER_ID).build();
        when(organizationMemberRepository.findByOrganizationId(ORG_ID)).thenReturn(List.of(member));
        assertThat(service.handle(new ListOrganizationMembersQuery(ORG_ID))).containsExactly(member);
    }

    @Test
    void pageOrganizationMembersReturnsMembers() {
        OrganizationMember member = OrganizationMember.builder().id(MEMBER_ID).build();
        when(organizationMemberRepository.findMembersByOrganizationId(eq(ORG_ID), any(Pageable.class))).thenReturn(List.of(member));
        assertThat(service.handle(new PageOrganizationMembersQuery(ORG_ID, 20, null)).items()).containsExactly(member);
    }
}
