package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.projects.api.DeleteProjectService;
import com.deepprotech.deepproject.projects.commands.DeleteProjectCommand;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProjectServiceImpl implements DeleteProjectService {

    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public void handle(DeleteProjectCommand command) {
        projectRepository.deleteById(command.projectId());
        log.info("project_deleted id={}", command.projectId());
    }
}
