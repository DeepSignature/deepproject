package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
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
    public List<Task> handle(ListTasksByProjectQuery query) {
        return taskRepository.findByProjectIdOrderByIdAsc(query.projectId());
    }

    @Override
    public List<Task> handle(ListSubtasksQuery query) {
        return taskRepository.findByParentTaskIdOrderByIdAsc(query.parentTaskId());
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
