package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.sprints.api.DeleteSprintService;
import com.deepprotech.deepproject.sprints.commands.DeleteSprintCommand;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteSprintServiceImpl implements DeleteSprintService {

    private final SprintRepository sprintRepository;

    @Override
    @Transactional
    public void handle(DeleteSprintCommand command) {
        sprintRepository.deleteById(command.sprintId());
        log.info("sprint_deleted id={}", command.sprintId());
    }
}
