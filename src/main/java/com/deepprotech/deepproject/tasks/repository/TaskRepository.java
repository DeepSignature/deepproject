package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
