package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.api.DeleteTaskService;
import com.deepprotech.deepproject.tasks.commands.DeleteTaskCommand;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteTaskServiceImpl implements DeleteTaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public void handle(DeleteTaskCommand command) {
        taskRepository.deleteById(command.taskId());
        log.info("task_deleted id={}", command.taskId());
    }
}
