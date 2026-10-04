package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.api.ManageTaskTagService;
import com.deepprotech.deepproject.tasks.commands.AddTaskTagCommand;
import com.deepprotech.deepproject.tasks.commands.RemoveTaskTagCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageTaskTagServiceImpl implements ManageTaskTagService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(AddTaskTagCommand command) {
        jdbc.update("INSERT INTO task_tags (task_id, tag_name) VALUES (?, ?) ON CONFLICT DO NOTHING",
                command.taskId(), command.tagName());
        log.info("task_tag_added taskId={} tag={}", command.taskId(), command.tagName());
    }

    @Override
    @Transactional
    public void handle(RemoveTaskTagCommand command) {
        jdbc.update("DELETE FROM task_tags WHERE task_id = ? AND tag_name = ?",
                command.taskId(), command.tagName());
        log.info("task_tag_removed taskId={} tag={}", command.taskId(), command.tagName());
    }
}
