package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.commands.CreateOrganizationCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrganizationServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateOrganizationServiceImpl service;

    @Test
    void createsOrgAndAddsCreatorAsAdmin() {
        CreateOrganizationCommand cmd = new CreateOrganizationCommand("my-org", "My Org", "Description", 1L);
        Organization org = Organization.builder().id(10L).identifier("my-org").name("My Org").description("Description").build();

        when(jdbc.query(eq("SELECT * FROM organizations WHERE identifier = ?"), any(RowMapper.class), eq("my-org")))
                .thenReturn(List.of(org));

        Organization result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getIdentifier()).isEqualTo("my-org");
        verify(jdbc).update(eq("INSERT INTO organizations (identifier, name, description) VALUES (?, ?, ?)"),
                eq("my-org"), eq("My Org"), eq("Description"));
        verify(jdbc).update(eq("INSERT INTO organization_members (organization_id, user_id, role) VALUES (?, ?, ?)"),
                eq(10L), eq(1L), eq("ORGANIZATION_ADMIN"));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenOrgNotFoundAfterInsert() {
        CreateOrganizationCommand cmd = new CreateOrganizationCommand("my-org", "My Org", "Description", 1L);
        when(jdbc.query(eq("SELECT * FROM organizations WHERE identifier = ?"), any(RowMapper.class), eq("my-org")))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
