package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.GetUserQueryService;
import com.deepprotech.deepproject.tasks.api.TaskDashboardQueryService;
import com.deepprotech.deepproject.tasks.constants.TaskPriority;
import com.deepprotech.deepproject.tasks.constants.TaskStatus;
import com.deepprotech.deepproject.tasks.constants.TaskType;
import com.deepprotech.deepproject.tasks.dto.AssigneeDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.PriorityCount;
import com.deepprotech.deepproject.tasks.dto.ProjectDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectStatisticsResponse;
import com.deepprotech.deepproject.tasks.dto.StatusCount;
import com.deepprotech.deepproject.tasks.dto.TaskCycleTime;
import com.deepprotech.deepproject.tasks.dto.TaskPeriodStatistics;
import com.deepprotech.deepproject.tasks.dto.TaskPriorityCount;
import com.deepprotech.deepproject.tasks.dto.TaskResponse;
import com.deepprotech.deepproject.tasks.dto.TaskStatusCount;
import com.deepprotech.deepproject.tasks.dto.TaskTypeCount;
import com.deepprotech.deepproject.tasks.dto.TypeCount;
import com.deepprotech.deepproject.tasks.queries.GetAssigneeDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectStatisticsQuery;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskDashboardQueryServiceImpl implements TaskDashboardQueryService {

    private final TaskRepository taskRepository;
    private final GetUserQueryService getUserQueryService;

    @Override
    public ProjectDashboardResponse getDashboard(GetProjectDashboardQuery query) {
        UUID projectId = query.projectId();
        Instant from = query.from();
        Instant to = query.to();

        long total = taskRepository.countByProjectId(projectId, null, from, to);
        long completed = taskRepository.countByProjectIdAndStatus(projectId, null, TaskStatus.DONE.name(), from, to);
        long overdue = taskRepository.countOverdue(projectId, null, TaskStatus.DONE.name(), Instant.now(), from, to);

        List<StatusCount> byStatus = buildStatusCounts(
                taskRepository.countGroupedByStatus(projectId, null, from, to));
        List<PriorityCount> byPriority = buildPriorityCounts(
                taskRepository.countGroupedByPriority(projectId, null, from, to));
        CursorPage<TaskResponse> tasks = allTasks(projectId, from, to, query.status(), query.priority(),
                query.taskLimit(), query.taskCursor());

        return new ProjectDashboardResponse(total, overdue, completed,
                completionPercentage(completed, total), byStatus, byPriority, tasks);
    }

    @Override
    public AssigneeDashboardResponse getAssigneeDashboard(GetAssigneeDashboardQuery query) {
        UUID projectId = query.projectId();
        UUID assigneeId = query.assigneeId();
        Instant from = query.from();
        Instant to = query.to();

        User assignee = getUserQueryService.getUserById(assigneeId);

        long total = taskRepository.countByProjectId(projectId, assigneeId, from, to);
        long completed = taskRepository.countByProjectIdAndStatus(projectId, assigneeId, TaskStatus.DONE.name(), from, to);
        long overdue = taskRepository.countOverdue(projectId, assigneeId, TaskStatus.DONE.name(), Instant.now(), from, to);

        List<StatusCount> byStatus = buildStatusCounts(
                taskRepository.countGroupedByStatus(projectId, assigneeId, from, to));
        List<PriorityCount> byPriority = buildPriorityCounts(
                taskRepository.countGroupedByPriority(projectId, assigneeId, from, to));
        CursorPage<TaskResponse> tasks = assigneeTasks(projectId, assigneeId, from, to, query.limit(), query.cursor());

        return new AssigneeDashboardResponse(assigneeId, displayName(assignee), total, overdue, completed,
                completionPercentage(completed, total), byStatus, byPriority, tasks);
    }

    @Override
    public ProjectStatisticsResponse getStatistics(GetProjectStatisticsQuery query) {
        UUID projectId = query.projectId();
        Instant from = query.from();
        Instant to = query.to();
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("'to' must be after 'from'");
        }

        Duration period = Duration.between(from, to);
        Instant previousFrom = from.minus(period);
        Instant previousTo = from;

        TaskPeriodStatistics current = buildPeriodStatistics(projectId, from, to);
        TaskPeriodStatistics previous = buildPeriodStatistics(projectId, previousFrom, previousTo);

        return new ProjectStatisticsResponse(projectId, from, to, previousFrom, previousTo, current, previous);
    }

    private TaskPeriodStatistics buildPeriodStatistics(UUID projectId, Instant from, Instant to) {
        long created = taskRepository.countCreatedInRange(projectId, from, to);
        long completed = taskRepository.countCompletedInRange(projectId, from, to);
        long overdue = taskRepository.countDueNotCompletedInRange(projectId, from, to, TaskStatus.DONE.name());
        double completionRate = created == 0 ? 0.0 : (double) completed / created;
        Double avgCycleTime = avgCycleTimeSeconds(
                taskRepository.findCompletedCycleTimes(projectId, from, to));
        BigDecimal estimatedHours = Optional.ofNullable(
                taskRepository.sumEstimatedHoursCreated(projectId, from, to)).orElse(BigDecimal.ZERO);
        BigDecimal actualHours = Optional.ofNullable(
                taskRepository.sumActualHoursCompleted(projectId, from, to)).orElse(BigDecimal.ZERO);
        List<StatusCount> byStatus = buildStatusCounts(
                taskRepository.countGroupedByStatusDue(projectId, from, to));
        List<PriorityCount> byPriority = buildPriorityCounts(
                taskRepository.countGroupedByPriorityDue(projectId, from, to));
        List<TypeCount> byType = buildTypeCounts(
                taskRepository.countGroupedByTypeDue(projectId, from, to));

        return new TaskPeriodStatistics(created, completed, overdue, completionRate, avgCycleTime,
                estimatedHours, actualHours, byStatus, byPriority, byType);
    }

    private static Double avgCycleTimeSeconds(List<TaskCycleTime> rows) {
        List<TaskCycleTime> valid = rows.stream()
                .filter(r -> r.getCreatedAt() != null && r.getCompletedAt() != null)
                .toList();
        if (valid.isEmpty()) {
            return null;
        }
        double sum = valid.stream()
                .mapToDouble(r -> Duration.between(r.getCreatedAt(), r.getCompletedAt()).toMillis() / 1000.0)
                .sum();
        return sum / valid.size();
    }

    private List<TypeCount> buildTypeCounts(List<TaskTypeCount> rows) {
        Map<String, Long> counts = new LinkedHashMap<>();
        Arrays.stream(TaskType.values()).forEach(t -> counts.put(t.name(), 0L));
        rows.forEach(r -> counts.put(r.getType(), r.getCount()));
        return counts.entrySet().stream()
                .map(e -> new TypeCount(e.getKey(), e.getValue()))
                .toList();
    }

    private CursorPage<TaskResponse> allTasks(UUID projectId, Instant from, Instant to,
                                              List<String> status, List<String> priority,
                                              int limit, String cursor) {
        List<String> normalizedStatus = normalize(status);
        List<String> normalizedPriority = normalize(priority);
        PageRequest pageable = PageRequest.of(0, limit + 1);
        CursorKey key = CursorCodec.decodeOrNull(cursor);
        List<Task> tasks = key == null
                ? taskRepository.findByProjectIdWithRange(projectId, from, to, normalizedStatus, normalizedPriority, pageable)
                : taskRepository.findByProjectIdWithRangeAfter(projectId, from, to, normalizedStatus, normalizedPriority,
                        key.createdAt(), key.id(), pageable);
        return CursorPages.build(tasks, limit, Task::getCreatedAt, Task::getId).map(TaskResponse::from);
    }

    private static List<String> normalize(List<String> values) {
        return values == null || values.isEmpty() ? null : values;
    }

    private CursorPage<TaskResponse> assigneeTasks(UUID projectId, UUID assigneeId, Instant from, Instant to,
                                                   int limit, String cursor) {
        PageRequest pageable = PageRequest.of(0, limit + 1);
        CursorKey key = CursorCodec.decodeOrNull(cursor);
        List<Task> tasks = key == null
                ? taskRepository.findByProjectIdAndAssignee(projectId, assigneeId, from, to, pageable)
                : taskRepository.findByProjectIdAndAssigneeAfter(projectId, assigneeId, from, to, key.createdAt(), key.id(), pageable);
        return CursorPages.build(tasks, limit, Task::getCreatedAt, Task::getId).map(TaskResponse::from);
    }

    private List<StatusCount> buildStatusCounts(List<TaskStatusCount> rows) {
        Map<String, Long> counts = new LinkedHashMap<>();
        Arrays.stream(TaskStatus.values()).forEach(s -> counts.put(s.name(), 0L));
        rows.forEach(r -> counts.put(r.getStatus(), r.getCount()));
        return counts.entrySet().stream()
                .map(e -> new StatusCount(e.getKey(), e.getValue()))
                .toList();
    }

    private List<PriorityCount> buildPriorityCounts(List<TaskPriorityCount> rows) {
        Map<String, Long> counts = new LinkedHashMap<>();
        Arrays.stream(TaskPriority.values()).forEach(p -> counts.put(p.name(), 0L));
        rows.forEach(r -> counts.put(r.getPriority(), r.getCount()));
        return counts.entrySet().stream()
                .map(e -> new PriorityCount(e.getKey(), e.getValue()))
                .toList();
    }

    private static double completionPercentage(long completed, long total) {
        return total == 0 ? 0.0 : (completed * 100.0) / total;
    }

    private static String displayName(User user) {
        if (user == null) {
            return null;
        }
        return user.getDisplayName() != null ? user.getDisplayName() : user.getUsername();
    }
}
