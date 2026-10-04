package com.deepprotech.deepproject.workspaces.web;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.api.CreateWorkspaceService;
import com.deepprotech.deepproject.workspaces.api.DeleteWorkspaceService;
import com.deepprotech.deepproject.workspaces.api.GetWorkspaceQueryService;
import com.deepprotech.deepproject.workspaces.api.UpdateWorkspaceService;
import com.deepprotech.deepproject.workspaces.commands.CreateWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.commands.DeleteWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.commands.UpdateWorkspaceCommand;
import com.deepprotech.deepproject.workspaces.dto.CreateWorkspaceRequest;
import com.deepprotech.deepproject.workspaces.dto.UpdateWorkspaceRequest;
import com.deepprotech.deepproject.workspaces.dto.WorkspaceResponse;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceByIdQuery;
import com.deepprotech.deepproject.workspaces.queries.ListUserWorkspacesQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final CreateWorkspaceService createWorkspaceService;
    private final UpdateWorkspaceService updateWorkspaceService;
    private final DeleteWorkspaceService deleteWorkspaceService;
    private final GetWorkspaceQueryService getWorkspaceQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_WORKSPACE_READ')")
    public ResponseEntity<List<WorkspaceResponse>> list(@RequestParam Long userId) {
        List<WorkspaceResponse> list = getWorkspaceQueryService.handle(new ListUserWorkspacesQuery(userId))
                .stream()
                .map(WorkspaceResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WORKSPACE_READ')")
    public ResponseEntity<WorkspaceResponse> get(@PathVariable Long id) {
        Workspace ws = getWorkspaceQueryService.handle(new GetWorkspaceByIdQuery(id));
        return ResponseEntity.ok(WorkspaceResponse.from(ws));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_WORKSPACE_CREATE')")
    public ResponseEntity<WorkspaceResponse> create(@Valid @RequestBody CreateWorkspaceRequest request,
                                                    @RequestParam Long ownerId) {
        Workspace ws = createWorkspaceService.handle(new CreateWorkspaceCommand(
                request.name(), request.slug(), request.description(), ownerId, request.organizationId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkspaceResponse.from(ws));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WORKSPACE_UPDATE')")
    public ResponseEntity<WorkspaceResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateWorkspaceRequest request) {
        Workspace ws = updateWorkspaceService.handle(new UpdateWorkspaceCommand(id, request.name(), request.description()));
        return ResponseEntity.ok(WorkspaceResponse.from(ws));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WORKSPACE_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteWorkspaceService.handle(new DeleteWorkspaceCommand(id));
        return ResponseEntity.noContent().build();
    }
}