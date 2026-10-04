package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.organizations.commands.DeleteOrganizationCommand;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteOrganizationServiceImplTest {

    @Mock OrganizationRepository organizationRepository;
    @InjectMocks DeleteOrganizationServiceImpl service;

    @Test
    void deletesOrg() {
        service.handle(new DeleteOrganizationCommand(10L));
        verify(organizationRepository).deleteById(10L);
    }
}
