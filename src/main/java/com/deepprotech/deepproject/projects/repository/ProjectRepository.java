package com.deepprotech.deepproject.projects.repository;

import com.deepprotech.deepproject.core.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE p.workspaceId = :workspaceId ORDER BY p.createdAt ASC, p.id ASC")
    List<Project> findByWorkspaceId(@Param("workspaceId") UUID workspaceId, Pageable pageable);

    @Query("""
            SELECT p FROM Project p
            WHERE p.workspaceId = :workspaceId
              AND (p.createdAt > :createdAt OR (p.createdAt = :createdAt AND p.id > :id))
            ORDER BY p.createdAt ASC, p.id ASC
            """)
    List<Project> findByWorkspaceIdAfter(@Param("workspaceId") UUID workspaceId,
                                         @Param("createdAt") Instant createdAt,
                                         @Param("id") UUID id,
                                         Pageable pageable);
}
