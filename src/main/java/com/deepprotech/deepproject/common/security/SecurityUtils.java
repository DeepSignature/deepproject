package com.deepprotech.deepproject.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<AuthenticatedUserPrincipal> currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.ofNullable(KeycloakJwtAuthenticationConverter.extractPrincipal(jwtAuth));
        }
        return Optional.empty();
    }

    public static Optional<String> currentIdentityId() {
        return currentPrincipal().map(AuthenticatedUserPrincipal::identityId);
    }

    public static Optional<String> currentUsername() {
        return currentPrincipal().map(AuthenticatedUserPrincipal::username);
    }
}