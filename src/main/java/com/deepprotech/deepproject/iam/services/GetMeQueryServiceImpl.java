package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.security.AppRole;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.GetMeQueryService;
import com.deepprotech.deepproject.iam.dto.UserProfileResponse;
import com.deepprotech.deepproject.iam.queries.GetMeQuery;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import com.deepprotech.deepproject.organizations.api.GetOrganizationQueryService;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMeQueryServiceImpl implements GetMeQueryService {

    private final UserRepository userRepository;
    private final GetOrganizationQueryService getOrganizationQueryService;

    @Override
    @Transactional
    public UserProfileResponse handle(GetMeQuery query) {
        User user = userRepository.findByIdentityId(query.identityId())
                .orElseGet(() -> provisionUser(query.identityId()));

        List<String> globalRoles = new ArrayList<>();
        List<String> globalPermissions = new ArrayList<>();
        for (String tokenRole : query.tokenRoles()) {
            AppRole appRole = AppRole.fromName(tokenRole);
            if (appRole != null) {
                globalRoles.add(appRole.name());
                appRole.getPermissions().forEach(p -> globalPermissions.add(p.name()));
            }
        }

        List<Organization> orgs = getOrganizationQueryService.handle(new ListUserOrganizationsQuery(user.getId()));

        List<UserProfileResponse.OrganizationMembership> memberships = new ArrayList<>();
        for (Organization org : orgs) {
            List<OrganizationMember> members = getOrganizationQueryService.handle(new ListOrganizationMembersQuery(org.getId()));
            Optional<OrganizationMember> memberOpt = members.stream()
                    .filter(m -> m.getUserId().equals(user.getId()))
                    .findFirst();
            if (memberOpt.isPresent()) {
                OrganizationMember member = memberOpt.get();
                AppRole orgRole = AppRole.fromName(member.getRole());
                List<String> orgPermissions = new ArrayList<>();
                if (orgRole != null) {
                    orgRole.getPermissions().forEach(p -> orgPermissions.add(p.name()));
                }
                memberships.add(new UserProfileResponse.OrganizationMembership(
                        org.getId(), org.getIdentifier(), org.getName(), member.getRole(), orgPermissions));
            }
        }

        return new UserProfileResponse(
                user.getIdentityId(), user.getId(), user.getUsername(), user.getEmail(), user.getDisplayName(),
                globalRoles, globalPermissions, memberships);
    }

    private User provisionUser(String identityId) {
        log.info("provisioning new user from Keycloak identity_id={}", identityId);
        String prefixedUsername = "kc_" + identityId.substring(0, Math.min(identityId.length(), 8));
        User user = User.builder()
                .identityId(identityId)
                .username(prefixedUsername)
                .email(identityId + "@placeholder")
                .displayName(prefixedUsername)
                .active(true)
                .build();
        return userRepository.save(user);
    }
}
