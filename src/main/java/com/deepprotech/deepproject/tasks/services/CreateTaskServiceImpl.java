package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.CreateTaskService;
import com.deepprotech.deepproject.tasks.commands.CreateTaskCommand;
import com.deepprotech.deepproject.tasks.events.TaskCreatedEvent;
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
public class CreateTaskServiceImpl implements CreateTaskService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Task handle(CreateTaskCommand command) {
        Task task = Task.builder()
                .projectId(command.projectId())
                .title(command.title())
                .description(command.description())
                .priority(command.priority() != null ? command.priority() : "MEDIUM")
                .taskType(command.taskType() != null ? command.taskType() : "TASK")
                .status("TODO")
                .build();

        task = taskRepository.save(task);

        eventPublisher.publishEvent(new TaskCreatedEvent(task.getId(), task.getTitle(), task.getProjectId(), Instant.now()));
        log.info("task_created id={} status={}", task.getId(), command.title());
        return task;
    }
}
