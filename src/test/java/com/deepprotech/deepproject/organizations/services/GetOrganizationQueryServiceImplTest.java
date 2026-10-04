package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdentifierQuery;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;
import com.deepprotech.deepproject.organizations.repository.OrganizationMemberRepository;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrganizationQueryServiceImplTest {

    @Mock OrganizationRepository organizationRepository;
    @Mock OrganizationMemberRepository organizationMemberRepository;
    @InjectMocks GetOrganizationQueryServiceImpl service;

    @Test
    void getByIdReturnsOrg() {
        Organization org = Organization.builder().id(10L).name("Acme").build();
        when(organizationRepository.findById(10L)).thenReturn(Optional.of(org));
        assertThat(service.handle(new GetOrganizationByIdQuery(10L))).isEqualTo(org);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(organizationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetOrganizationByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByIdentifierReturnsOrg() {
        Organization org = Organization.builder().id(10L).identifier("acme").build();
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
        Organization org = Organization.builder().id(10L).build();
        when(organizationRepository.findOrganizationsByUserId(1L)).thenReturn(List.of(org));
        assertThat(service.handle(new ListUserOrganizationsQuery(1L))).containsExactly(org);
    }

    @Test
    void listOrganizationMembersReturnsMembers() {
        OrganizationMember member = OrganizationMember.builder().id(1L).build();
        when(organizationMemberRepository.findByOrganizationId(10L)).thenReturn(List.of(member));
        assertThat(service.handle(new ListOrganizationMembersQuery(10L))).containsExactly(member);
    }
}
