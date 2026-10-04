package com.deepprotech.deepproject.common;

import org.springframework.lang.Nullable;

public final class AuditContext {

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private AuditContext() {
    }

    public static void setCurrentUser(String user) {
        CURRENT_USER.set(user);
    }

    @Nullable
    public static String getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}