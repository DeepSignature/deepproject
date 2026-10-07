package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.organizations.commands.DeleteOrganizationCommand;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteOrganizationServiceImplTest {

    private static final UUID ORG_ID = UUID.fromString("a0000003-0000-0000-0000-000000000001");

    @Mock OrganizationRepository organizationRepository;
    @InjectMocks DeleteOrganizationServiceImpl service;

    @Test
    void deletesOrg() {
        service.handle(new DeleteOrganizationCommand(ORG_ID));
        verify(organizationRepository).deleteById(ORG_ID);
    }
}
