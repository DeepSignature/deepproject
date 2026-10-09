package com.deepprotech.deepproject.tasks.web;

import com.deepprotech.deepproject.tasks.api.TaskDashboardQueryService;
import com.deepprotech.deepproject.tasks.dto.AssigneeDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectDashboardResponse;
import com.deepprotech.deepproject.tasks.queries.GetAssigneeDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectDashboardQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/dashboard")
@RequiredArgsConstructor
@Tag(name = "Task Dashboard", description = "Read-only task dashboard aggregations")
public class TaskDashboardController {

    private final TaskDashboardQueryService taskDashboardQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_TASK_READ')")
    @Operation(summary = "Get project task dashboard", description = "Aggregated task statistics for a project with optional due-date range filtering.")
    public ResponseEntity<ProjectDashboardResponse> dashboard(
            @PathVariable UUID projectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int recentLimit,
            @RequestParam(required = false) String recentCursor) {
        return ResponseEntity.ok(taskDashboardQueryService.getDashboard(
                new GetProjectDashboardQuery(projectId, from, to, recentLimit, recentCursor)));
    }

    @GetMapping("/assignee/{assigneeId}/task")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_READ')")
    @Operation(summary = "Get assignee task dashboard", description = "Aggregated statistics and paginated tasks for a single assignee within a project.")
    public ResponseEntity<AssigneeDashboardResponse> assigneeDashboard(
            @PathVariable UUID projectId,
            @PathVariable UUID assigneeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(taskDashboardQueryService.getAssigneeDashboard(
                new GetAssigneeDashboardQuery(projectId, assigneeId, from, to, limit, cursor)));
    }
}
