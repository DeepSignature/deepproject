package com.deepprotech.deepproject.iam.repository;

import com.deepprotech.deepproject.core.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdentityId(String identityId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
