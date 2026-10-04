package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.workspaces.api.DeleteWorkspaceService;
import com.deepprotech.deepproject.workspaces.commands.DeleteWorkspaceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteWorkspaceServiceImpl implements DeleteWorkspaceService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(DeleteWorkspaceCommand command) {
        jdbc.update("DELETE FROM workspaces WHERE id = ?", command.workspaceId());
        log.info("workspace_deleted id={}", command.workspaceId());
    }
}
