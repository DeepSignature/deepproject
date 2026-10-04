package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.workspaces.api.DeleteWorkspaceService;
import com.deepprotech.deepproject.workspaces.commands.DeleteWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteWorkspaceServiceImpl implements DeleteWorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Override
    @Transactional
    public void handle(DeleteWorkspaceCommand command) {
        workspaceRepository.deleteById(command.workspaceId());
        log.info("workspace_deleted id={}", command.workspaceId());
    }
}
