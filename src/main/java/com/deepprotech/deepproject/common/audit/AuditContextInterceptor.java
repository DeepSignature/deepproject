package com.deepprotech.deepproject.common.audit;

import com.deepprotech.deepproject.common.AuditContext;
import com.deepprotech.deepproject.common.security.KeycloakJwtAuthenticationConverter;
import com.deepprotech.deepproject.common.security.AuthenticatedUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditContextInterceptor implements HandlerInterceptor {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = resolveUsername();
        AuditContext.setCurrentUser(username != null ? username : "anonymous");

        try {
            jdbcTemplate.queryForObject("SELECT set_config('app.current_user', ?, true)", String.class, AuditContext.getCurrentUser());
        } catch (Exception ex) {
            log.warn("Failed to set app.current_user: {}", ex.getMessage());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) {
        AuditContext.clear();
    }

    @Nullable
    private String resolveUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            AuthenticatedUserPrincipal principal = KeycloakJwtAuthenticationConverter.extractPrincipal(jwtAuth);
            if (principal != null) {
                return principal.username();
            }
            return jwtAuth.getName();
        }
        if (auth != null) {
            return auth.getName();
        }
        return null;
    }
}