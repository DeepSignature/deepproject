package com.deepprotech.deepproject.iam.repository;

import com.deepprotech.deepproject.core.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByIdentityId(String identityId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u ORDER BY u.createdAt ASC, u.id ASC")
    List<User> findAllOrderByCreatedAtAscIdAsc(Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE (u.createdAt > :createdAt OR (u.createdAt = :createdAt AND u.id > :id))
            ORDER BY u.createdAt ASC, u.id ASC
            """)
    List<User> findUsersAfter(@Param("createdAt") Instant createdAt, @Param("id") UUID id, Pageable pageable);

    List<User> findByIdIn(Collection<UUID> ids);
}
