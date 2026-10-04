package com.deepprotech.deepproject.common.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record AuthenticatedUserPrincipal(
        String identityId,
        String username,
        String email,
        String displayName,
        List<String> roles,
        List<String> permissions
) {
    public Collection<String> getAuthorities() {
        var authorities = new ArrayList<String>();
        roles.forEach(role -> authorities.add("ROLE_" + role));
        permissions.forEach(perm -> authorities.add("PERMISSION_" + perm));
        return authorities;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}