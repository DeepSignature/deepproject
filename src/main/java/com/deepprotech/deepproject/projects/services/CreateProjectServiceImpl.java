package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.CreateProjectService;
import com.deepprotech.deepproject.projects.commands.CreateProjectCommand;
import com.deepprotech.deepproject.projects.events.ProjectCreatedEvent;
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
public class CreateProjectServiceImpl implements CreateProjectService {

    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Project handle(CreateProjectCommand command) {
        Project p = Project.builder()
                .workspaceId(command.workspaceId())
                .name(command.name())
                .description(command.description())
                .status("ACTIVE")
                .build();

        p = projectRepository.save(p);

        eventPublisher.publishEvent(new ProjectCreatedEvent(p.getId(), p.getName(), p.getWorkspaceId(), Instant.now()));
        log.info("project_created id={} name={}", p.getId(), command.name());
        return p;
    }
}
