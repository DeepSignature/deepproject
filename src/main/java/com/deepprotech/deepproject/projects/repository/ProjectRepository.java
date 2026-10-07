package com.deepprotech.deepproject.projects.repository;

import com.deepprotech.deepproject.core.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByWorkspaceIdOrderByIdAsc(UUID workspaceId);
}
