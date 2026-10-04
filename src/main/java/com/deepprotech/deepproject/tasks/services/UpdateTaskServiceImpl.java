package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.UpdateTaskService;
import com.deepprotech.deepproject.tasks.commands.UpdateTaskCommand;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTaskServiceImpl implements UpdateTaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public Task handle(UpdateTaskCommand command) {
        Task task = taskRepository.findById(command.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", command.taskId()));

        if (command.title() != null) {
            task.setTitle(command.title());
        }
        if (command.description() != null) {
            task.setDescription(command.description());
        }
        if (command.priority() != null) {
            task.setPriority(command.priority());
        }

        Task updated = taskRepository.save(task);
        log.info("task_updated id={}", command.taskId());
        return updated;
    }
}
