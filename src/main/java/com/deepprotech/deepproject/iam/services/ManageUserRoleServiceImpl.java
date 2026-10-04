package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.api.ManageUserRoleService;
import com.deepprotech.deepproject.iam.commands.AssignUserRoleCommand;
import com.deepprotech.deepproject.iam.commands.RemoveUserRoleCommand;
import com.deepprotech.deepproject.iam.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageUserRoleServiceImpl implements ManageUserRoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void handle(AssignUserRoleCommand command) {
        roleRepository.assignRoleToUser(command.userId(), command.roleId());
        log.info("role_assigned userId={} roleId={}", command.userId(), command.roleId());
    }

    @Override
    @Transactional
    public void handle(RemoveUserRoleCommand command) {
        roleRepository.removeRoleFromUser(command.userId(), command.roleId());
        log.info("role_removed userId={} roleId={}", command.userId(), command.roleId());
    }
}
