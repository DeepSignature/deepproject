package com.deepprotech.deepproject.workspaces.repository;

import com.deepprotech.deepproject.core.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    Optional<Workspace> findBySlug(String slug);

    @Query("SELECT w FROM Workspace w WHERE w.id IN (SELECT wm.workspaceId FROM WorkspaceMember wm WHERE wm.userId = :userId)")
    List<Workspace> findWorkspacesByUserId(@Param("userId") Long userId);
}
