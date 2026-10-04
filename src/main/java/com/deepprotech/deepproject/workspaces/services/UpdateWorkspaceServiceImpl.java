package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.api.UpdateWorkspaceService;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateWorkspaceServiceImpl implements UpdateWorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Override
    @Transactional
    public Workspace handle(UpdateWorkspaceCommand command) {
        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", command.workspaceId()));

        if (command.name() != null) {
            ws.setName(command.name());
        }
        if (command.description() != null) {
            ws.setDescription(command.description());
        }

        Workspace updated = workspaceRepository.save(ws);
        log.info("workspace_updated id={}", command.workspaceId());
        return updated;
    }
}
