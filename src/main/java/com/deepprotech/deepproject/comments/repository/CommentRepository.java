package com.deepprotech.deepproject.comments.repository;

import com.deepprotech.deepproject.core.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c WHERE c.taskId = :taskId ORDER BY c.createdAt ASC, c.id ASC")
    List<Comment> findByTaskId(@Param("taskId") UUID taskId, Pageable pageable);

    @Query("""
            SELECT c FROM Comment c
            WHERE c.taskId = :taskId
              AND (c.createdAt > :createdAt OR (c.createdAt = :createdAt AND c.id > :id))
            ORDER BY c.createdAt ASC, c.id ASC
            """)
    List<Comment> findByTaskIdAfter(@Param("taskId") UUID taskId,
                                    @Param("createdAt") Instant createdAt,
                                    @Param("id") UUID id,
                                    Pageable pageable);
}
