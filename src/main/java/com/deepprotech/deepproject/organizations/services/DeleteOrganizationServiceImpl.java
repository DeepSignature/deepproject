package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.organizations.api.DeleteOrganizationService;
import com.deepprotech.deepproject.organizations.commands.DeleteOrganizationCommand;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteOrganizationServiceImpl implements DeleteOrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public void handle(DeleteOrganizationCommand command) {
        organizationRepository.deleteById(command.organizationId());
        log.info("organization_deleted id={}", command.organizationId());
    }
}
