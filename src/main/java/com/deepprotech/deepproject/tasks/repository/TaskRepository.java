package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectIdOrderByIdAsc(UUID projectId);

    List<Task> findByParentTaskIdOrderByIdAsc(UUID parentTaskId);
}
