package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdentifierQuery;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrganizationQueryServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks GetOrganizationQueryServiceImpl service;

    private final Organization org = Organization.builder().id(1L).identifier("my-org").name("My Org").description("Desc").build();

    @Test
    void getByIdReturnsOrg() {
        when(jdbc.query(eq("SELECT * FROM organizations WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(org));
        assertThat(service.handle(new GetOrganizationByIdQuery(1L)).getId()).isEqualTo(1L);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM organizations WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetOrganizationByIdQuery(999L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByIdentifierReturnsOrg() {
        when(jdbc.query(eq("SELECT * FROM organizations WHERE identifier = ?"), any(RowMapper.class), eq("my-org"))).thenReturn(List.of(org));
        assertThat(service.handle(new GetOrganizationByIdentifierQuery("my-org")).getIdentifier()).isEqualTo("my-org");
    }

    @Test
    void getByIdentifierThrowsWhenNotFound() {
        when(jdbc.query(eq("SELECT * FROM organizations WHERE identifier = ?"), any(RowMapper.class), eq("unknown"))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.handle(new GetOrganizationByIdentifierQuery("unknown")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listUserOrganizationsReturnsOrgs() {
        when(jdbc.query(any(String.class), any(RowMapper.class), eq(1L))).thenReturn(List.of(org));
        assertThat(service.handle(new ListUserOrganizationsQuery(1L))).hasSize(1);
    }

    @Test
    void listOrganizationMembersReturnsMembers() {
        OrganizationMember member = OrganizationMember.builder().id(1L).organizationId(1L).userId(2L).role("ORGANIZATION_ADMIN").build();
        when(jdbc.query(eq("SELECT * FROM organization_members WHERE organization_id = ?"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(member));
        List<OrganizationMember> result = service.handle(new ListOrganizationMembersQuery(1L));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo("ORGANIZATION_ADMIN");
    }
}
