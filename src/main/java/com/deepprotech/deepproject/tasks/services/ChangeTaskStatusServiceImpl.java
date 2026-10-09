package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.ChangeTaskStatusService;
import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;
import com.deepprotech.deepproject.tasks.constants.TaskStatus;
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
    public void handle(ChangeTaskStatusCommand command) {
        Task task = taskRepository.findById(command.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", command.taskId()));

        TaskStatus newStatus = command.status();
        TaskStatus oldStatus = TaskStatus.valueOf(task.getStatus());

        validateChangeStatus(oldStatus, newStatus);

        updateTask(task, newStatus.name());

        eventPublisher.publishEvent(new TaskStatusChangedEvent(
                command.taskId(),
                oldStatus.name(),
                newStatus.name(),
                Instant.now()));

        log.info("task_status_changed id={} {} -> {}", command.taskId(), oldStatus, command.status());
    }

    private void validateChangeStatus(TaskStatus oldStatus, TaskStatus newStatus){

        if(oldStatus.equals(newStatus)){
            return;
        }

        if(!newStatus.equals(TaskStatus.BLOCKED)
                &&
                newStatus.ordinal() < oldStatus.ordinal())
        {
            throw new IllegalArgumentException(
                    String.format("Cannot change task status backwards, from %s to %s", oldStatus, newStatus
                    ));
        }

    }
    private void updateTask(Task task, String newStatus){
        task.setStatus(newStatus);
        task.setUpdatedAt(Instant.now());

        taskRepository.save(task);
    }

}
