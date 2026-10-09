package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.GetUserQueryService;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUsersByIdsQuery;
import com.deepprotech.deepproject.tasks.api.TaskDashboardQueryService;
import com.deepprotech.deepproject.tasks.constants.TaskPriority;
import com.deepprotech.deepproject.tasks.constants.TaskStatus;
import com.deepprotech.deepproject.tasks.dto.AssigneeCount;
import com.deepprotech.deepproject.tasks.dto.AssigneeDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.AssigneeTaskCount;
import com.deepprotech.deepproject.tasks.dto.PriorityCount;
import com.deepprotech.deepproject.tasks.dto.ProjectDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.StatusCount;
import com.deepprotech.deepproject.tasks.dto.TaskPriorityCount;
import com.deepprotech.deepproject.tasks.dto.TaskResponse;
import com.deepprotech.deepproject.tasks.dto.TaskStatusCount;
import com.deepprotech.deepproject.tasks.queries.GetAssigneeDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectDashboardQuery;
import com.deepprotech.deepproject.tasks.repository.TaskAssigneeRepository;
import com.deepprotech.deepproject.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskDashboardQueryServiceImpl implements TaskDashboardQueryService {

    private final TaskRepository taskRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;
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
        List<AssigneeCount> perAssignee = buildAssigneeCounts(
                taskAssigneeRepository.countGroupedByAssignee(projectId, from, to));
        CursorPage<TaskResponse> recentlyCompleted = recentlyCompleted(projectId, from, to, query.recentLimit(), query.recentCursor());

        return new ProjectDashboardResponse(total, overdue, completed,
                completionPercentage(completed, total), byStatus, byPriority, perAssignee, recentlyCompleted);
    }

    @Override
    public AssigneeDashboardResponse getAssigneeDashboard(GetAssigneeDashboardQuery query) {
        UUID projectId = query.projectId();
        UUID assigneeId = query.assigneeId();
        Instant from = query.from();
        Instant to = query.to();

        User assignee = getUserQueryService.handle(new GetUserByIdQuery(assigneeId));

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

    private CursorPage<TaskResponse> recentlyCompleted(UUID projectId, Instant from, Instant to, int limit, String cursor) {
        PageRequest pageable = PageRequest.of(0, limit + 1);
        CursorKey key = CursorCodec.decodeOrNull(cursor);
        List<Task> tasks = key == null
                ? taskRepository.findRecentlyCompleted(projectId, from, to, pageable)
                : taskRepository.findRecentlyCompletedBefore(projectId, from, to, key.createdAt(), key.id(), pageable);
        return CursorPages.build(tasks, limit, Task::getCompletedAt, Task::getId).map(TaskResponse::from);
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

    private List<AssigneeCount> buildAssigneeCounts(List<AssigneeTaskCount> rows) {
        if (rows.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = rows.stream().map(AssigneeTaskCount::getAssigneeId).toList();
        Map<UUID, User> users = getUserQueryService.handle(new GetUsersByIdsQuery(Set.copyOf(ids)));
        return rows.stream()
                .map(r -> {
                    User user = users.get(r.getAssigneeId());
                    return new AssigneeCount(r.getAssigneeId(), displayName(user), r.getCount());
                })
                .sorted(Comparator.comparing(AssigneeCount::displayName))
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
