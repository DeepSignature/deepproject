package com.deepprotech.deepproject.common.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {
        String identityId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String displayName = jwt.getClaimAsString("name");

        List<String> tokenRoles = extractRoles(jwt);

        List<String> globalPermissions = new ArrayList<>();
        List<String> mappedRoles = new ArrayList<>();
        for (String tokenRole : tokenRoles) {
            AppRole appRole = AppRole.fromName(tokenRole);
            if (appRole != null) {
                mappedRoles.add(appRole.name());
                appRole.getPermissions().forEach(p -> globalPermissions.add(p.name()));
            }
        }

        AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(
                identityId, username != null ? username : identityId, email, displayName, mappedRoles, globalPermissions);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        principal.getAuthorities().forEach(a -> authorities.add(new SimpleGrantedAuthority(a)));

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
        token.setDetails(principal);
        return token;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            return (List<String>) realmAccess.get("roles");
        }

        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> client = (Map<String, Object>) entry.getValue();
                    if (client.containsKey("roles")) {
                        return (List<String>) client.get("roles");
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    public static AuthenticatedUserPrincipal extractPrincipal(JwtAuthenticationToken token) {
        Object details = token.getDetails();
        if (details instanceof AuthenticatedUserPrincipal principal) {
            return principal;
        }
        return null;
    }
}