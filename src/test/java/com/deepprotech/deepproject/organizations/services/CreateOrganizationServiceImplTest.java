package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.commands.CreateOrganizationCommand;
import com.deepprotech.deepproject.organizations.repository.OrganizationMemberRepository;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrganizationServiceImplTest {

    @Mock OrganizationRepository organizationRepository;
    @Mock OrganizationMemberRepository organizationMemberRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateOrganizationServiceImpl service;

    @Test
    void createsOrgAndAddsCreatorAsAdmin() {
        CreateOrganizationCommand cmd = new CreateOrganizationCommand("acme", "Acme Inc", "Desc", 1L);
        Organization org = Organization.builder().id(10L).identifier("acme").name("Acme Inc").description("Desc").build();
        when(organizationRepository.save(any(Organization.class))).thenReturn(org);

        Organization result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getIdentifier()).isEqualTo("acme");
        verify(organizationRepository).save(any(Organization.class));
        verify(organizationMemberRepository).save(any(OrganizationMember.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
