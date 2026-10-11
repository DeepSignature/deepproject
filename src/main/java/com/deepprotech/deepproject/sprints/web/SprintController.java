package com.deepprotech.deepproject.sprints.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.api.ChangeSprintStatusService;
import com.deepprotech.deepproject.sprints.api.CreateSprintService;
import com.deepprotech.deepproject.sprints.api.DeleteSprintService;
import com.deepprotech.deepproject.sprints.api.GetSprintQueryService;
import com.deepprotech.deepproject.sprints.api.UpdateSprintService;
import com.deepprotech.deepproject.sprints.commands.ChangeSprintStatusCommand;
import com.deepprotech.deepproject.sprints.commands.CreateSprintCommand;
import com.deepprotech.deepproject.sprints.commands.DeleteSprintCommand;
import com.deepprotech.deepproject.sprints.commands.UpdateSprintCommand;
import com.deepprotech.deepproject.sprints.dto.CreateSprintRequest;
import com.deepprotech.deepproject.sprints.dto.SprintResponse;
import com.deepprotech.deepproject.sprints.dto.UpdateSprintRequest;
import com.deepprotech.deepproject.sprints.queries.GetSprintByIdQuery;
import com.deepprotech.deepproject.sprints.queries.ListSprintsByProjectQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final CreateSprintService createSprintService;
    private final UpdateSprintService updateSprintService;
    private final ChangeSprintStatusService changeSprintStatusService;
    private final DeleteSprintService deleteSprintService;
    private final GetSprintQueryService getSprintQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_SPRINT_READ')")
    public ResponseEntity<CursorPage<SprintResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String cursor) {
        CursorPage<SprintResponse> list = getSprintQueryService.handle(new ListSprintsByProjectQuery(projectId, limit, cursor))
                .map(SprintResponse::from);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_SPRINT_READ')")
    public ResponseEntity<SprintResponse> get(@PathVariable UUID id) {
        Sprint sprint = getSprintQueryService.handle(new GetSprintByIdQuery(id));
        return ResponseEntity.ok(SprintResponse.from(sprint));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_SPRINT_CREATE')")
    public ResponseEntity<SprintResponse> create(@PathVariable UUID projectId,
                                                 @Valid @RequestBody CreateSprintRequest request) {
        Sprint sprint = createSprintService.handle(new CreateSprintCommand(projectId, request.name(), request.description(), request.startDate(), request.endDate()));
        return ResponseEntity.status(HttpStatus.CREATED).body(SprintResponse.from(sprint));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_SPRINT_UPDATE')")
    public ResponseEntity<SprintResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateSprintRequest request) {
        Sprint sprint = updateSprintService.handle(new UpdateSprintCommand(id, request.name(), request.description(), request.startDate(), request.endDate()));
        return ResponseEntity.ok(SprintResponse.from(sprint));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PERMISSION_SPRINT_MANAGE')")
    public ResponseEntity<SprintResponse> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        Sprint sprint = changeSprintStatusService.handle(new ChangeSprintStatusCommand(id, status));
        return ResponseEntity.ok(SprintResponse.from(sprint));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_SPRINT_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteSprintService.handle(new DeleteSprintCommand(id));
        return ResponseEntity.noContent().build();
    }
}
