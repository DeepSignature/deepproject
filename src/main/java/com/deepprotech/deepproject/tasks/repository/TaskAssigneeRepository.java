package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.TaskAssignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, UUID> {

    List<TaskAssignee> findByTaskId(UUID taskId);

    Optional<TaskAssignee> findByTaskIdAndUserId(UUID taskId, UUID userId);

    void deleteByTaskIdAndUserId(UUID taskId, UUID userId);
}
