package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.commands.AddOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.RemoveOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationMemberRoleCommand;
import com.deepprotech.deepproject.organizations.repository.OrganizationMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageOrganizationMemberServiceImplTest {

    @Mock OrganizationMemberRepository organizationMemberRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ManageOrganizationMemberServiceImpl service;

    @Test
    void addsMember() {
        when(organizationMemberRepository.findByOrganizationIdAndUserId(10L, 1L)).thenReturn(Optional.empty());
        service.handle(new AddOrganizationMemberCommand(10L, 1L, "ORGANIZATION_MEMBER"));
        verify(organizationMemberRepository).save(any(OrganizationMember.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void removesMember() {
        service.handle(new RemoveOrganizationMemberCommand(10L, 1L));
        verify(organizationMemberRepository).deleteByOrganizationIdAndUserId(10L, 1L);
    }

    @Test
    void updatesMemberRole() {
        OrganizationMember member = OrganizationMember.builder().organizationId(10L).userId(1L).role("ORGANIZATION_MEMBER").build();
        when(organizationMemberRepository.findByOrganizationIdAndUserId(10L, 1L)).thenReturn(Optional.of(member));
        service.handle(new UpdateOrganizationMemberRoleCommand(10L, 1L, "ORGANIZATION_ADMIN"));
        verify(organizationMemberRepository).save(member);
    }
}
