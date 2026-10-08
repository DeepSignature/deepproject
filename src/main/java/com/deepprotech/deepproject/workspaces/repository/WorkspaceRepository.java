package com.deepprotech.deepproject.workspaces.repository;

import com.deepprotech.deepproject.core.Workspace;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    Optional<Workspace> findBySlug(String slug);

    @Query("""
            SELECT w FROM Workspace w
            WHERE w.id IN (SELECT wm.workspaceId FROM WorkspaceMember wm WHERE wm.userId = :userId)
            ORDER BY w.createdAt ASC, w.id ASC
            """)
    List<Workspace> findWorkspacesByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT w FROM Workspace w
            WHERE w.id IN (SELECT wm.workspaceId FROM WorkspaceMember wm WHERE wm.userId = :userId)
              AND (w.createdAt > :createdAt OR (w.createdAt = :createdAt AND w.id > :id))
            ORDER BY w.createdAt ASC, w.id ASC
            """)
    List<Workspace> findWorkspacesByUserIdAfter(@Param("userId") UUID userId,
                                                @Param("createdAt") Instant createdAt,
                                                @Param("id") UUID id,
                                                Pageable pageable);
}
