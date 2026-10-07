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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageOrganizationMemberServiceImplTest {

    private static final UUID ORG_ID = UUID.fromString("a0000003-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Mock OrganizationMemberRepository organizationMemberRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ManageOrganizationMemberServiceImpl service;

    @Test
    void addsMember() {
        when(organizationMemberRepository.findByOrganizationIdAndUserId(ORG_ID, USER_ID)).thenReturn(Optional.empty());
        service.handle(new AddOrganizationMemberCommand(ORG_ID, USER_ID, "ORGANIZATION_MEMBER"));
        verify(organizationMemberRepository).save(any(OrganizationMember.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void removesMember() {
        service.handle(new RemoveOrganizationMemberCommand(ORG_ID, USER_ID));
        verify(organizationMemberRepository).deleteByOrganizationIdAndUserId(ORG_ID, USER_ID);
    }

    @Test
    void updatesMemberRole() {
        OrganizationMember member = OrganizationMember.builder().organizationId(ORG_ID).userId(USER_ID).role("ORGANIZATION_MEMBER").build();
        when(organizationMemberRepository.findByOrganizationIdAndUserId(ORG_ID, USER_ID)).thenReturn(Optional.of(member));
        service.handle(new UpdateOrganizationMemberRoleCommand(ORG_ID, USER_ID, "ORGANIZATION_ADMIN"));
        verify(organizationMemberRepository).save(member);
    }
}
