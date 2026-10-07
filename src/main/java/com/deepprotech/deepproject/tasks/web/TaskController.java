package com.deepprotech.deepproject.tasks.web;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.*;
import com.deepprotech.deepproject.tasks.commands.*;
import com.deepprotech.deepproject.tasks.dto.CreateTaskRequest;
import com.deepprotech.deepproject.tasks.dto.TaskResponse;
import com.deepprotech.deepproject.tasks.dto.UpdateTaskRequest;
import com.deepprotech.deepproject.tasks.queries.GetTaskByIdQuery;
import com.deepprotech.deepproject.tasks.queries.ListSubtasksQuery;
import com.deepprotech.deepproject.tasks.queries.ListTasksByProjectQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskService createTaskService;
    private final UpdateTaskService updateTaskService;
    private final ChangeTaskStatusService changeTaskStatusService;
    private final AssignTaskService assignTaskService;
    private final ManageTaskTagService manageTaskTagService;
    private final DeleteTaskService deleteTaskService;
    private final GetTaskQueryService getTaskQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_TASK_READ')")
    public ResponseEntity<List<TaskResponse>> list(@PathVariable UUID projectId) {
        List<TaskResponse> list = getTaskQueryService.handle(new ListTasksByProjectQuery(projectId))
                .stream()
                .map(TaskResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_READ')")
    public ResponseEntity<TaskResponse> get(@PathVariable UUID id) {
        Task t = getTaskQueryService.handle(new GetTaskByIdQuery(id));
        return ResponseEntity.ok(TaskResponse.from(t));
    }

    @GetMapping("/{id}/subtasks")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_READ')")
    public ResponseEntity<List<TaskResponse>> subtasks(@PathVariable UUID id) {
        List<TaskResponse> list = getTaskQueryService.handle(new ListSubtasksQuery(id))
                .stream()
                .map(TaskResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_TASK_CREATE')")
    public ResponseEntity<TaskResponse> create(@PathVariable UUID projectId,
                                               @Valid @RequestBody CreateTaskRequest request) {
        Task t = createTaskService.handle(new CreateTaskCommand(
                projectId, request.title(), request.description(), request.priority(), request.taskType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(t));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_UPDATE')")
    public ResponseEntity<TaskResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateTaskRequest request) {
        Task t = updateTaskService.handle(new UpdateTaskCommand(id, request.title(), request.description(), request.priority()));
        return ResponseEntity.ok(TaskResponse.from(t));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_STATUS_CHANGE')")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        Task t = changeTaskStatusService.handle(new ChangeTaskStatusCommand(id, status));
        return ResponseEntity.ok(TaskResponse.from(t));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_ASSIGN')")
    public ResponseEntity<Void> assign(@PathVariable UUID id, @RequestParam UUID userId) {
        assignTaskService.handle(new AssignTaskUserCommand(id, userId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_ASSIGN')")
    public ResponseEntity<Void> unassign(@PathVariable UUID id, @PathVariable UUID userId) {
        assignTaskService.handle(new UnassignTaskUserCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/tags")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_UPDATE')")
    public ResponseEntity<Void> addTag(@PathVariable UUID id, @RequestParam String tagName) {
        manageTaskTagService.handle(new AddTaskTagCommand(id, tagName));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/tags/{tagName}")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_UPDATE')")
    public ResponseEntity<Void> removeTag(@PathVariable UUID id, @PathVariable String tagName) {
        manageTaskTagService.handle(new RemoveTaskTagCommand(id, tagName));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteTaskService.handle(new DeleteTaskCommand(id));
        return ResponseEntity.noContent().build();
    }
}