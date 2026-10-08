package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.api.GetTaskQueryService;
import com.deepprotech.deepproject.tasks.queries.GetTaskByIdQuery;
import com.deepprotech.deepproject.tasks.queries.ListSubtasksQuery;
import com.deepprotech.deepproject.tasks.queries.ListTaskAssigneesQuery;
import com.deepprotech.deepproject.tasks.queries.ListTaskTagsQuery;
import com.deepprotech.deepproject.tasks.queries.ListTasksByProjectQuery;
import com.deepprotech.deepproject.tasks.repository.TaskAssigneeRepository;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import com.deepprotech.deepproject.tasks.repository.TaskTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTaskQueryServiceImpl implements GetTaskQueryService {

    private final TaskRepository taskRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;
    private final TaskTagRepository taskTagRepository;

    @Override
    public Task handle(GetTaskByIdQuery query) {
        return taskRepository.findById(query.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", query.taskId()));
    }

    @Override
    public CursorPage<Task> handle(ListTasksByProjectQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        List<Task> tasks = key == null
                ? taskRepository.findByProjectId(query.projectId(), pageable)
                : taskRepository.findByProjectIdAfter(query.projectId(), key.createdAt(), key.id(), pageable);
        return CursorPages.build(tasks, query.limit(), Task::getCreatedAt, Task::getId);
    }

    @Override
    public CursorPage<Task> handle(ListSubtasksQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        List<Task> tasks = key == null
                ? taskRepository.findByParentTaskId(query.parentTaskId(), pageable)
                : taskRepository.findByParentTaskIdAfter(query.parentTaskId(), key.createdAt(), key.id(), pageable);
        return CursorPages.build(tasks, query.limit(), Task::getCreatedAt, Task::getId);
    }

    @Override
    public List<TaskAssignee> handle(ListTaskAssigneesQuery query) {
        return taskAssigneeRepository.findByTaskId(query.taskId());
    }

    @Override
    public List<TaskTag> handle(ListTaskTagsQuery query) {
        return taskTagRepository.findByTaskId(query.taskId());
    }
}
