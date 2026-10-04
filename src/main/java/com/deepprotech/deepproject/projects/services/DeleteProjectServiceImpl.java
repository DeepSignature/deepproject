package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.projects.api.DeleteProjectService;
import com.deepprotech.deepproject.projects.commands.DeleteProjectCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProjectServiceImpl implements DeleteProjectService {

    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void handle(DeleteProjectCommand command) {
        jdbc.update("DELETE FROM projects WHERE id = ?", command.projectId());
        log.info("project_deleted id={}", command.projectId());
    }
}
