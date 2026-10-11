package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.api.UpdateSprintService;
import com.deepprotech.deepproject.sprints.commands.UpdateSprintCommand;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateSprintServiceImpl implements UpdateSprintService {

    private final SprintRepository sprintRepository;

    @Override
    @Transactional
    public Sprint handle(UpdateSprintCommand command) {
        Sprint sprint = sprintRepository.findById(command.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", command.sprintId()));

        if (command.name() != null) {
            sprint.setName(command.name());
        }
        if (command.description() != null) {
            sprint.setDescription(command.description());
        }
        if (command.startDate() != null) {
            sprint.setStartDate(command.startDate());
        }
        if (command.endDate() != null) {
            sprint.setEndDate(command.endDate());
        }

        Sprint updated = sprintRepository.save(sprint);
        log.info("sprint_updated id={}", command.sprintId());
        return updated;
    }
}
