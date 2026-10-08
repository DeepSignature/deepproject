package com.deepprotech.deepproject.organizations.repository;

import com.deepprotech.deepproject.core.OrganizationMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {

    List<OrganizationMember> findByOrganizationId(UUID organizationId);

    Optional<OrganizationMember> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    void deleteByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    @Query("SELECT m FROM OrganizationMember m WHERE m.organizationId = :organizationId ORDER BY m.createdAt ASC, m.id ASC")
    List<OrganizationMember> findMembersByOrganizationId(@Param("organizationId") UUID organizationId, Pageable pageable);

    @Query("""
            SELECT m FROM OrganizationMember m
            WHERE m.organizationId = :organizationId
              AND (m.createdAt > :createdAt OR (m.createdAt = :createdAt AND m.id > :id))
            ORDER BY m.createdAt ASC, m.id ASC
            """)
    List<OrganizationMember> findMembersByOrganizationIdAfter(@Param("organizationId") UUID organizationId,
                                                              @Param("createdAt") Instant createdAt,
                                                              @Param("id") UUID id,
                                                              Pageable pageable);
}
