package com.deepprotech.deepproject.tasks.web;

import com.deepprotech.deepproject.tasks.api.TaskDashboardQueryService;
import com.deepprotech.deepproject.tasks.constants.TaskPriority;
import com.deepprotech.deepproject.tasks.constants.TaskStatus;
import com.deepprotech.deepproject.tasks.dto.AssigneeDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectAttentionResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectStatisticsResponse;
import com.deepprotech.deepproject.tasks.queries.GetAssigneeDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectAttentionQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectStatisticsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
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
import java.util.List;
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
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int taskLimit,
            @RequestParam(required = false) String taskCursor,
            @Parameter(explode = Explode.FALSE) @RequestParam(required = false) List<TaskStatus> status,
            @Parameter(explode = Explode.FALSE) @RequestParam(required = false) List<TaskPriority> priority) {
        return ResponseEntity.ok(taskDashboardQueryService.getDashboard(
                new GetProjectDashboardQuery(projectId, from, to, taskLimit, taskCursor,
                        toNames(status), toNames(priority))));
    }

    private static <E extends Enum<E>> List<String> toNames(List<E> values) {
        return values == null ? null : values.stream().map(Enum::name).toList();
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

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_READ')")
    @Operation(summary = "Get project task statistics", description = "Task statistics for a date range plus the preceding equal-length period, for trend comparison.")
    public ResponseEntity<ProjectStatisticsResponse> statistics(
            @PathVariable UUID projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(taskDashboardQueryService.getStatistics(
                new GetProjectStatisticsQuery(projectId, from, to)));
    }

    @GetMapping("/attention")
    @PreAuthorize("hasAuthority('PERMISSION_TASK_READ')")
    @Operation(summary = "Get project task attention list", description = "Overdue, highest-priority open, and stale tasks needing attention.")
    public ResponseEntity<ProjectAttentionResponse> attention(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
        return ResponseEntity.ok(taskDashboardQueryService.getAttention(
                new GetProjectAttentionQuery(projectId, limit)));
    }
}
