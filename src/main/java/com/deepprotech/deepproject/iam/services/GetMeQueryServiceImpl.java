package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.security.AppRole;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.GetMeQueryService;
import com.deepprotech.deepproject.iam.dto.UserProfileResponse;
import com.deepprotech.deepproject.iam.queries.GetMeQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMeQueryServiceImpl implements GetMeQueryService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .identityId(rs.getString("identity_id"))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .displayName(rs.getString("display_name"))
            .active(rs.getBoolean("active"))
            .build();

    private static final RowMapper<Organization> ORG_MAPPER = (rs, rowNum) -> Organization.builder()
            .id(rs.getLong("id"))
            .identifier(rs.getString("identifier"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .build();

    private static final RowMapper<OrganizationMember> OM_MAPPER = (rs, rowNum) -> OrganizationMember.builder()
            .id(rs.getLong("id"))
            .organizationId(rs.getLong("organization_id"))
            .userId(rs.getLong("user_id"))
            .role(rs.getString("role"))
            .build();

    @Override
    public UserProfileResponse handle(GetMeQuery query) {
        List<User> users = jdbc.query("SELECT * FROM users WHERE identity_id = ?", USER_MAPPER, query.identityId());
        User user;
        if (users.isEmpty()) {
            user = provisionUser(query.identityId());
        } else {
            user = users.get(0);
        }

        List<String> globalRoles = new ArrayList<>();
        List<String> globalPermissions = new ArrayList<>();
        for (String tokenRole : query.tokenRoles()) {
            AppRole appRole = AppRole.fromName(tokenRole);
            if (appRole != null) {
                globalRoles.add(appRole.name());
                appRole.getPermissions().forEach(p -> globalPermissions.add(p.name()));
            }
        }

        List<Organization> orgs = jdbc.query(
                "SELECT o.* FROM organizations o INNER JOIN organization_members om ON o.id = om.organization_id WHERE om.user_id = ?",
                ORG_MAPPER, user.getId());

        List<UserProfileResponse.OrganizationMembership> memberships = new ArrayList<>();
        for (Organization org : orgs) {
            List<OrganizationMember> members = jdbc.query(
                    "SELECT * FROM organization_members WHERE organization_id = ? AND user_id = ?",
                    OM_MAPPER, org.getId(), user.getId());
            if (!members.isEmpty()) {
                OrganizationMember member = members.get(0);
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
        jdbc.update(
                "INSERT INTO users (identity_id, username, email, display_name) VALUES (?, ?, ?, ?) ON CONFLICT (identity_id) DO NOTHING",
                identityId, prefixedUsername, identityId + "@placeholder", prefixedUsername);
        return jdbc.query("SELECT * FROM users WHERE identity_id = ?", USER_MAPPER, identityId).get(0);
    }
}