package com.deepprotech.deepproject.projects.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.*;
import com.deepprotech.deepproject.projects.commands.ChangeProjectStatusCommand;
import com.deepprotech.deepproject.projects.commands.CreateProjectCommand;
import com.deepprotech.deepproject.projects.commands.DeleteProjectCommand;
import com.deepprotech.deepproject.projects.commands.UpdateProjectCommand;
import com.deepprotech.deepproject.projects.dto.CreateProjectRequest;
import com.deepprotech.deepproject.projects.dto.ProjectResponse;
import com.deepprotech.deepproject.projects.dto.UpdateProjectRequest;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final CreateProjectService createProjectService;
    private final UpdateProjectService updateProjectService;
    private final ChangeProjectStatusService changeProjectStatusService;
    private final DeleteProjectService deleteProjectService;
    private final GetProjectQueryService getProjectQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_PROJECT_READ')")
    public ResponseEntity<CursorPage<ProjectResponse>> list(
            @PathVariable UUID workspaceId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String cursor) {
        CursorPage<ProjectResponse> list = getProjectQueryService.handle(new ListProjectsByWorkspaceQuery(workspaceId, limit, cursor))
                .map(ProjectResponse::from);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PROJECT_READ')")
    public ResponseEntity<ProjectResponse> get(@PathVariable UUID id) {
        Project p = getProjectQueryService.handle(new GetProjectByIdQuery(id));
        return ResponseEntity.ok(ProjectResponse.from(p));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_PROJECT_CREATE')")
    public ResponseEntity<ProjectResponse> create(@PathVariable UUID workspaceId,
                                                  @Valid @RequestBody CreateProjectRequest request) {
        Project p = createProjectService.handle(new CreateProjectCommand(workspaceId, request.name(), request.description()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.from(p));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PROJECT_UPDATE')")
    public ResponseEntity<ProjectResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateProjectRequest request) {
        Project p = updateProjectService.handle(new UpdateProjectCommand(id, request.name(), request.description()));
        return ResponseEntity.ok(ProjectResponse.from(p));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PERMISSION_PROJECT_UPDATE')")
    public ResponseEntity<ProjectResponse> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        Project p = changeProjectStatusService.handle(new ChangeProjectStatusCommand(id, status));
        return ResponseEntity.ok(ProjectResponse.from(p));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PROJECT_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteProjectService.handle(new DeleteProjectCommand(id));
        return ResponseEntity.noContent().build();
    }
}