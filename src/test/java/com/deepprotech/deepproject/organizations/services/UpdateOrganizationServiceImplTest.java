package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationCommand;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateOrganizationServiceImplTest {

    @Mock OrganizationRepository organizationRepository;
    @InjectMocks UpdateOrganizationServiceImpl service;

    @Test
    void updatesOrgReturnsIt() {
        UpdateOrganizationCommand cmd = new UpdateOrganizationCommand(10L, "New Name", "New Desc");
        Organization org = Organization.builder().id(10L).identifier("acme").name("Old Name").description("Old Desc").build();
        when(organizationRepository.findById(10L)).thenReturn(Optional.of(org));
        when(organizationRepository.save(any(Organization.class))).thenAnswer(inv -> inv.getArgument(0));

        Organization result = service.handle(cmd);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        verify(organizationRepository).save(org);
    }

    @Test
    void throwsWhenOrgNotFound() {
        UpdateOrganizationCommand cmd = new UpdateOrganizationCommand(999L, "Name", "Desc");
        when(organizationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
