package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.ChangeProjectStatusService;
import com.deepprotech.deepproject.projects.commands.ChangeProjectStatusCommand;
import com.deepprotech.deepproject.projects.events.ProjectStatusChangedEvent;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeProjectStatusServiceImpl implements ChangeProjectStatusService {

    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Project handle(ChangeProjectStatusCommand command) {
        Project project = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        String oldStatus = project.getStatus();
        project.setStatus(command.status());
        Project updated = projectRepository.save(project);

        eventPublisher.publishEvent(new ProjectStatusChangedEvent(command.projectId(), oldStatus, command.status(), Instant.now()));
        log.info("project_status_changed id={} {} -> {}", command.projectId(), oldStatus, command.status());
        return updated;
    }
}
