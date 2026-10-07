package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.TaskTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTag, UUID> {

    List<TaskTag> findByTaskId(UUID taskId);

    Optional<TaskTag> findByTaskIdAndTagName(UUID taskId, String tagName);

    void deleteByTaskIdAndTagName(UUID taskId, String tagName);
}
