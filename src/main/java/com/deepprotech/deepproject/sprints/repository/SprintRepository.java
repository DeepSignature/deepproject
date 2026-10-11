package com.deepprotech.deepproject.sprints.repository;

import com.deepprotech.deepproject.core.Sprint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, UUID> {

    @Query("SELECT s FROM Sprint s WHERE s.projectId = :projectId ORDER BY s.createdAt ASC, s.id ASC")
    List<Sprint> findByProjectId(@Param("projectId") UUID projectId, Pageable pageable);

    @Query("""
            SELECT s FROM Sprint s
            WHERE s.projectId = :projectId
              AND (s.createdAt > :createdAt OR (s.createdAt = :createdAt AND s.id > :id))
            ORDER BY s.createdAt ASC, s.id ASC
            """)
    List<Sprint> findByProjectIdAfter(@Param("projectId") UUID projectId,
                                      @Param("createdAt") Instant createdAt,
                                      @Param("id") UUID id,
                                      Pageable pageable);
}
