package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationCommand;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateOrganizationServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks UpdateOrganizationServiceImpl service;

    @Test
    void updatesOrgReturnsIt() {
        UpdateOrganizationCommand cmd = new UpdateOrganizationCommand(1L, "New Name", "New Desc");
        Organization org = Organization.builder().id(1L).identifier("my-org").name("New Name").description("New Desc").build();

        when(jdbc.query(eq("SELECT * FROM organizations WHERE id = ?"), any(RowMapper.class), eq(1L))).thenReturn(List.of(org));

        Organization result = service.handle(cmd);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(jdbc).update(eq("UPDATE organizations SET name = ?, description = ?, updated_at = ? WHERE id = ?"),
                eq("New Name"), eq("New Desc"), any(), eq(1L));
    }

    @Test
    void throwsWhenOrgNotFound() {
        UpdateOrganizationCommand cmd = new UpdateOrganizationCommand(999L, "New Name", "New Desc");
        when(jdbc.query(eq("SELECT * FROM organizations WHERE id = ?"), any(RowMapper.class), eq(999L))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
