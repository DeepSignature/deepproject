package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.api.GetTaskQueryService;
import com.deepprotech.deepproject.tasks.queries.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTaskQueryServiceImpl implements GetTaskQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Task> TASK_MAPPER = (rs, rowNum) -> Task.builder()
            .id(rs.getLong("id"))
            .projectId(rs.getLong("project_id"))
            .parentTaskId(rs.getObject("parent_task_id", Long.class))
            .title(rs.getString("title"))
            .description(rs.getString("description"))
            .status(rs.getString("status"))
            .priority(rs.getString("priority"))
            .taskType(rs.getString("task_type"))
            .build();

    private static final RowMapper<TaskAssignee> TA_MAPPER = (rs, rowNum) -> TaskAssignee.builder()
            .id(rs.getLong("id"))
            .taskId(rs.getLong("task_id"))
            .userId(rs.getLong("user_id"))
            .build();

    private static final RowMapper<TaskTag> TT_MAPPER = (rs, rowNum) -> TaskTag.builder()
            .id(rs.getLong("id"))
            .taskId(rs.getLong("task_id"))
            .tagName(rs.getString("tag_name"))
            .build();

    @Override
    public Task handle(GetTaskByIdQuery query) {
        List<Task> list = jdbc.query("SELECT * FROM tasks WHERE id = ?", TASK_MAPPER, query.taskId());
        if (list.isEmpty()) throw new ResourceNotFoundException("Task", query.taskId());
        return list.get(0);
    }

    @Override
    public List<Task> handle(ListTasksByProjectQuery query) {
        return jdbc.query("SELECT * FROM tasks WHERE project_id = ? AND parent_task_id IS NULL ORDER BY id",
                TASK_MAPPER, query.projectId());
    }

    @Override
    public List<Task> handle(ListSubtasksQuery query) {
        return jdbc.query("SELECT * FROM tasks WHERE parent_task_id = ? ORDER BY id",
                TASK_MAPPER, query.parentTaskId());
    }

    @Override
    public List<TaskAssignee> handle(ListTaskAssigneesQuery query) {
        return jdbc.query("SELECT * FROM task_assignees WHERE task_id = ?", TA_MAPPER, query.taskId());
    }

    @Override
    public List<TaskTag> handle(ListTaskTagsQuery query) {
        return jdbc.query("SELECT * FROM task_tags WHERE task_id = ?", TT_MAPPER, query.taskId());
    }
}
