package com.deepprotech.deepproject.organizations.repository;

import com.deepprotech.deepproject.core.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByIdentifier(String identifier);

    @Query("SELECT o FROM Organization o WHERE o.id IN (SELECT om.organizationId FROM OrganizationMember om WHERE om.userId = :userId)")
    List<Organization> findOrganizationsByUserId(@Param("userId") UUID userId);
}
