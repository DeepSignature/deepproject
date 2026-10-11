package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.api.ChangeSprintStatusService;
import com.deepprotech.deepproject.sprints.commands.ChangeSprintStatusCommand;
import com.deepprotech.deepproject.sprints.constants.SprintStatus;
import com.deepprotech.deepproject.sprints.events.SprintStatusChangedEvent;
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
public class ChangeSprintStatusServiceImpl implements ChangeSprintStatusService {

    private final SprintRepository sprintRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Sprint handle(ChangeSprintStatusCommand command) {
        Sprint sprint = sprintRepository.findById(command.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", command.sprintId()));

        SprintStatus newStatus = parseStatus(command.status());
        String oldStatus = sprint.getStatus();
        sprint.setStatus(newStatus.name());
        sprint.setActive(newStatus == SprintStatus.ACTIVE);

        Sprint updated = sprintRepository.save(sprint);

        eventPublisher.publishEvent(new SprintStatusChangedEvent(command.sprintId(), oldStatus, newStatus.name(), Instant.now()));
        log.info("sprint_status_changed id={} {} -> {}", command.sprintId(), oldStatus, newStatus.name());
        return updated;
    }

    private static SprintStatus parseStatus(String status) {
        try {
            return SprintStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sprint status: " + status);
        }
    }
}
