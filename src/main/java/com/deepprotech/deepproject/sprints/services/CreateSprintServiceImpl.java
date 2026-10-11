package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.api.CreateSprintService;
import com.deepprotech.deepproject.sprints.commands.CreateSprintCommand;
import com.deepprotech.deepproject.sprints.constants.SprintStatus;
import com.deepprotech.deepproject.sprints.events.SprintCreatedEvent;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateSprintServiceImpl implements CreateSprintService {

    private final SprintRepository sprintRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Sprint handle(CreateSprintCommand command) {
        Sprint sprint = Sprint.builder()
                .projectId(command.projectId())
                .name(command.name())
                .description(command.description())
                .startDate(command.startDate())
                .endDate(command.endDate())
                .status(SprintStatus.PLANNED.name())
                .build();

        sprint = sprintRepository.save(sprint);

        eventPublisher.publishEvent(new SprintCreatedEvent(sprint.getId(), sprint.getName(), sprint.getProjectId(), Instant.now()));
        log.info("sprint_created id={} name={}", sprint.getId(), command.name());
        return sprint;
    }
}
