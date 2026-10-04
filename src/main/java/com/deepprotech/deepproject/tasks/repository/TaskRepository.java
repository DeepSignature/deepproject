package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectIdOrderByIdAsc(Long projectId);

    List<Task> findByParentTaskIdOrderByIdAsc(Long parentTaskId);
}
