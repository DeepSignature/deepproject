package com.deepprotech.deepproject.tasks.repository;

import com.deepprotech.deepproject.core.TaskTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTag, Long> {

    List<TaskTag> findByTaskId(Long taskId);

    Optional<TaskTag> findByTaskIdAndTagName(Long taskId, String tagName);

    void deleteByTaskIdAndTagName(Long taskId, String tagName);
}
