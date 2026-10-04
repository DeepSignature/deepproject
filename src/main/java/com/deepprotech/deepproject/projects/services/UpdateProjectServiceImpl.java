package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.UpdateProjectService;
import com.deepprotech.deepproject.projects.commands.UpdateProjectCommand;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProjectServiceImpl implements UpdateProjectService {

    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public Project handle(UpdateProjectCommand command) {
        Project project = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        if (command.name() != null) {
            project.setName(command.name());
        }
        if (command.description() != null) {
            project.setDescription(command.description());
        }

        Project updated = projectRepository.save(project);
        log.info("project_updated id={}", command.projectId());
        return updated;
    }
}
