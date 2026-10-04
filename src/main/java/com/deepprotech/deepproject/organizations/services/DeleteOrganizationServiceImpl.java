package com.deepprotech.deepproject.organizations.services;

import com.deepprotech.deepproject.organizations.api.DeleteOrganizationService;
import com.deepprotech.deepproject.organizations.commands.DeleteOrganizationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteOrganizationServiceImpl implements DeleteOrganizationService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(DeleteOrganizationCommand command) {
        jdbc.update("DELETE FROM organizations WHERE id = ?", command.organizationId());
        log.info("organization_deleted id={}", command.organizationId());
    }
}