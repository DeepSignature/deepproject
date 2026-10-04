package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.api.DeleteTaskService;
import com.deepprotech.deepproject.tasks.commands.DeleteTaskCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteTaskServiceImpl implements DeleteTaskService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(DeleteTaskCommand command) {
        jdbc.update("DELETE FROM tasks WHERE id = ?", command.taskId());
        log.info("task_deleted id={}", command.taskId());
    }
}
