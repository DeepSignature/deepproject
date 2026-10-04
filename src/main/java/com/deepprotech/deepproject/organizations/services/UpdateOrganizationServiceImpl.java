package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.api.UpdateOrganizationService;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationCommand;
import com.deepprotech.deepproject.organizations.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrganizationServiceImpl implements UpdateOrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public Organization handle(UpdateOrganizationCommand command) {
        Organization orgEntity = organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", command.organizationId()));

        if (command.name() != null) {
            orgEntity.setName(command.name());
        }
        if (command.description() != null) {
            orgEntity.setDescription(command.description());
        }

        Organization updated = organizationRepository.save(orgEntity);
        log.info("organization_updated id={}", command.organizationId());
        return updated;
    }
}
