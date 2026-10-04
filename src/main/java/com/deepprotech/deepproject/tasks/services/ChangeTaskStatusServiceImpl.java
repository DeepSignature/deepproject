package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.ChangeTaskStatusService;
import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;
import com.deepprotech.deepproject.tasks.events.TaskStatusChangedEvent;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeTaskStatusServiceImpl implements ChangeTaskStatusService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Task handle(ChangeTaskStatusCommand command) {
        Task task = taskRepository.findById(command.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", command.taskId()));

        String oldStatus = task.getStatus();
        task.setStatus(command.status());
        Task updated = taskRepository.save(task);

        eventPublisher.publishEvent(new TaskStatusChangedEvent(command.taskId(), oldStatus, command.status(), Instant.now()));
        log.info("task_status_changed id={} {} -> {}", command.taskId(), oldStatus, command.status());
        return updated;
    }
}
