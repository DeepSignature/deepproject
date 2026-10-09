package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.dto.TaskCycleTime;
import com.deepprotech.deepproject.tasks.dto.TaskPriorityCount;
import com.deepprotech.deepproject.tasks.dto.TaskStatusCount;
import com.deepprotech.deepproject.tasks.dto.TaskTypeCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.projectId = :projectId ORDER BY t.createdAt ASC, t.id ASC")
    List<Task> findByProjectId(@Param("projectId") UUID projectId, Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND (t.createdAt > :createdAt OR (t.createdAt = :createdAt AND t.id > :id))
            ORDER BY t.createdAt ASC, t.id ASC
            """)
    List<Task> findByProjectIdAfter(@Param("projectId") UUID projectId,
                                    @Param("createdAt") Instant createdAt,
                                    @Param("id") UUID id,
                                    Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.parentTaskId = :parentTaskId ORDER BY t.createdAt ASC, t.id ASC")
    List<Task> findByParentTaskId(@Param("parentTaskId") UUID parentTaskId, Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.parentTaskId = :parentTaskId
              AND (t.createdAt > :createdAt OR (t.createdAt = :createdAt AND t.id > :id))
            ORDER BY t.createdAt ASC, t.id ASC
            """)
    List<Task> findByParentTaskIdAfter(@Param("parentTaskId") UUID parentTaskId,
                                       @Param("createdAt") Instant createdAt,
                                       @Param("id") UUID id,
                                       Pageable pageable);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.projectId = :projectId
              AND (:assigneeId IS NULL OR t.id IN (SELECT a.taskId FROM TaskAssignee a WHERE a.userId = :assigneeId))
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
            """)
    long countByProjectId(@Param("projectId") UUID projectId,
                          @Param("assigneeId") UUID assigneeId,
                          @Param("from") Instant from,
                          @Param("to") Instant to);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.projectId = :projectId
              AND t.status = :status
              AND (:assigneeId IS NULL OR t.id IN (SELECT a.taskId FROM TaskAssignee a WHERE a.userId = :assigneeId))
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
            """)
    long countByProjectIdAndStatus(@Param("projectId") UUID projectId,
                                   @Param("assigneeId") UUID assigneeId,
                                   @Param("status") String status,
                                   @Param("from") Instant from,
                                   @Param("to") Instant to);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.projectId = :projectId
              AND t.status <> :excludedStatus
              AND t.dueDate IS NOT NULL
              AND t.dueDate < :now
              AND (:assigneeId IS NULL OR t.id IN (SELECT a.taskId FROM TaskAssignee a WHERE a.userId = :assigneeId))
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
            """)
    long countOverdue(@Param("projectId") UUID projectId,
                      @Param("assigneeId") UUID assigneeId,
                      @Param("excludedStatus") String excludedStatus,
                      @Param("now") Instant now,
                      @Param("from") Instant from,
                      @Param("to") Instant to);

    @Query("""
            SELECT t.status AS status, COUNT(t) AS count FROM Task t
            WHERE t.projectId = :projectId
              AND (:assigneeId IS NULL OR t.id IN (SELECT a.taskId FROM TaskAssignee a WHERE a.userId = :assigneeId))
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
            GROUP BY t.status
            """)
    List<TaskStatusCount> countGroupedByStatus(@Param("projectId") UUID projectId,
                                               @Param("assigneeId") UUID assigneeId,
                                               @Param("from") Instant from,
                                               @Param("to") Instant to);

    @Query("""
            SELECT t.priority AS priority, COUNT(t) AS count FROM Task t
            WHERE t.projectId = :projectId
              AND (:assigneeId IS NULL OR t.id IN (SELECT a.taskId FROM TaskAssignee a WHERE a.userId = :assigneeId))
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
            GROUP BY t.priority
            """)
    List<TaskPriorityCount> countGroupedByPriority(@Param("projectId") UUID projectId,
                                                   @Param("assigneeId") UUID assigneeId,
                                                   @Param("from") Instant from,
                                                   @Param("to") Instant to);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND t.id IN (SELECT a.taskId FROM TaskAssignee a WHERE a.userId = :assigneeId)
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
            ORDER BY t.createdAt ASC, t.id ASC
            """)
    List<Task> findByProjectIdAndAssignee(@Param("projectId") UUID projectId,
                                          @Param("assigneeId") UUID assigneeId,
                                          @Param("from") Instant from,
                                          @Param("to") Instant to,
                                          Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND t.id IN (SELECT a.taskId FROM TaskAssignee a WHERE a.userId = :assigneeId)
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
              AND (t.createdAt > :createdAt OR (t.createdAt = :createdAt AND t.id > :id))
            ORDER BY t.createdAt ASC, t.id ASC
            """)
    List<Task> findByProjectIdAndAssigneeAfter(@Param("projectId") UUID projectId,
                                               @Param("assigneeId") UUID assigneeId,
                                               @Param("from") Instant from,
                                               @Param("to") Instant to,
                                               @Param("createdAt") Instant createdAt,
                                               @Param("id") UUID id,
                                               Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
              AND (:status IS NULL OR t.status IN :status)
              AND (:priority IS NULL OR t.priority IN :priority)
            ORDER BY t.createdAt ASC, t.id ASC
            """)
    List<Task> findByProjectIdWithRange(@Param("projectId") UUID projectId,
                                        @Param("from") Instant from,
                                        @Param("to") Instant to,
                                        @Param("status") List<String> status,
                                        @Param("priority") List<String> priority,
                                        Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND (CAST(:from AS instant) IS NULL OR t.dueDate >= :from)
              AND (CAST(:to AS instant) IS NULL OR t.dueDate <= :to)
              AND (:status IS NULL OR t.status IN :status)
              AND (:priority IS NULL OR t.priority IN :priority)
              AND (t.createdAt > :createdAt OR (t.createdAt = :createdAt AND t.id > :id))
            ORDER BY t.createdAt ASC, t.id ASC
            """)
    List<Task> findByProjectIdWithRangeAfter(@Param("projectId") UUID projectId,
                                             @Param("from") Instant from,
                                             @Param("to") Instant to,
                                             @Param("status") List<String> status,
                                             @Param("priority") List<String> priority,
                                             @Param("createdAt") Instant createdAt,
                                             @Param("id") UUID id,
                                             Pageable pageable);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.projectId = :projectId
              AND t.createdAt >= :from AND t.createdAt < :to
            """)
    long countCreatedInRange(@Param("projectId") UUID projectId,
                             @Param("from") Instant from,
                             @Param("to") Instant to);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.projectId = :projectId
              AND t.completedAt >= :from AND t.completedAt < :to
            """)
    long countCompletedInRange(@Param("projectId") UUID projectId,
                               @Param("from") Instant from,
                               @Param("to") Instant to);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.projectId = :projectId
              AND t.dueDate >= :from AND t.dueDate < :to
              AND t.status <> :excludedStatus
            """)
    long countDueNotCompletedInRange(@Param("projectId") UUID projectId,
                                     @Param("from") Instant from,
                                     @Param("to") Instant to,
                                     @Param("excludedStatus") String excludedStatus);

    @Query("""
            SELECT t.status AS status, COUNT(t) AS count FROM Task t
            WHERE t.projectId = :projectId
              AND t.dueDate >= :from AND t.dueDate < :to
            GROUP BY t.status
            """)
    List<TaskStatusCount> countGroupedByStatusDue(@Param("projectId") UUID projectId,
                                                  @Param("from") Instant from,
                                                  @Param("to") Instant to);

    @Query("""
            SELECT t.priority AS priority, COUNT(t) AS count FROM Task t
            WHERE t.projectId = :projectId
              AND t.dueDate >= :from AND t.dueDate < :to
            GROUP BY t.priority
            """)
    List<TaskPriorityCount> countGroupedByPriorityDue(@Param("projectId") UUID projectId,
                                                      @Param("from") Instant from,
                                                      @Param("to") Instant to);

    @Query("""
            SELECT t.taskType AS type, COUNT(t) AS count FROM Task t
            WHERE t.projectId = :projectId
              AND t.dueDate >= :from AND t.dueDate < :to
            GROUP BY t.taskType
            """)
    List<TaskTypeCount> countGroupedByTypeDue(@Param("projectId") UUID projectId,
                                              @Param("from") Instant from,
                                              @Param("to") Instant to);

    @Query("""
            SELECT SUM(t.estimatedHours) FROM Task t
            WHERE t.projectId = :projectId
              AND t.createdAt >= :from AND t.createdAt < :to
            """)
    BigDecimal sumEstimatedHoursCreated(@Param("projectId") UUID projectId,
                                        @Param("from") Instant from,
                                        @Param("to") Instant to);

    @Query("""
            SELECT SUM(t.actualHours) FROM Task t
            WHERE t.projectId = :projectId
              AND t.completedAt >= :from AND t.completedAt < :to
            """)
    BigDecimal sumActualHoursCompleted(@Param("projectId") UUID projectId,
                                       @Param("from") Instant from,
                                       @Param("to") Instant to);

    @Query("""
            SELECT t.createdAt AS createdAt, t.completedAt AS completedAt FROM Task t
            WHERE t.projectId = :projectId
              AND t.completedAt >= :from AND t.completedAt < :to
            """)
    List<TaskCycleTime> findCompletedCycleTimes(@Param("projectId") UUID projectId,
                                                @Param("from") Instant from,
                                                @Param("to") Instant to);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND t.status <> :excludedStatus
              AND t.dueDate IS NOT NULL
              AND t.dueDate < :now
            ORDER BY t.dueDate DESC, t.id DESC
            """)
    List<Task> findOverdueTasks(@Param("projectId") UUID projectId,
                                @Param("excludedStatus") String excludedStatus,
                                @Param("now") Instant now,
                                Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND t.status <> :excludedStatus
              AND t.priority = :priority
            ORDER BY t.createdAt DESC, t.id DESC
            """)
    List<Task> findUrgentOpenTasks(@Param("projectId") UUID projectId,
                                   @Param("excludedStatus") String excludedStatus,
                                   @Param("priority") String priority,
                                   Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.projectId = :projectId
              AND t.status <> :excludedStatus
              AND t.createdAt < :cutoff
            ORDER BY t.createdAt ASC, t.id ASC
            """)
    List<Task> findStaleTasks(@Param("projectId") UUID projectId,
                              @Param("excludedStatus") String excludedStatus,
                              @Param("cutoff") Instant cutoff,
                              Pageable pageable);
}
