package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.tasks.api.AssignTaskService;
import com.deepprotech.deepproject.tasks.commands.AssignTaskUserCommand;
import com.deepprotech.deepproject.tasks.commands.UnassignTaskUserCommand;
import com.deepprotech.deepproject.tasks.events.TaskAssignedEvent;
import com.deepprotech.deepproject.tasks.repository.TaskAssigneeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignTaskServiceImpl implements AssignTaskService {

    private final TaskAssigneeRepository taskAssigneeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(AssignTaskUserCommand command) {
        if (taskAssigneeRepository.findByTaskIdAndUserId(command.taskId(), command.userId()).isEmpty()) {
            TaskAssignee assignee = TaskAssignee.builder()
                    .taskId(command.taskId())
                    .userId(command.userId())
                    .build();
            taskAssigneeRepository.save(assignee);
        }
        eventPublisher.publishEvent(new TaskAssignedEvent(command.taskId(), command.userId(), Instant.now()));
        log.info("task_assigned taskId={} userId={}", command.taskId(), command.userId());
    }

    @Override
    @Transactional
    public void handle(UnassignTaskUserCommand command) {
        taskAssigneeRepository.deleteByTaskIdAndUserId(command.taskId(), command.userId());
        log.info("task_unassigned taskId={} userId={}", command.taskId(), command.userId());
    }
}
