package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.tasks.dto.AssigneeTaskCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, UUID> {

    List<TaskAssignee> findByTaskId(UUID taskId);

    Optional<TaskAssignee> findByTaskIdAndUserId(UUID taskId, UUID userId);

    void deleteByTaskIdAndUserId(UUID taskId, UUID userId);

    @Query("""
            SELECT a.userId AS assigneeId, COUNT(a) AS count FROM TaskAssignee a
            WHERE a.taskId IN (
                SELECT t.id FROM Task t
                WHERE t.projectId = :projectId
                  AND (:from IS NULL OR t.dueDate >= :from)
                  AND (:to IS NULL OR t.dueDate <= :to)
            )
            GROUP BY a.userId
            """)
    List<AssigneeTaskCount> countGroupedByAssignee(@Param("projectId") UUID projectId,
                                                   @Param("from") Instant from,
                                                   @Param("to") Instant to);
}
