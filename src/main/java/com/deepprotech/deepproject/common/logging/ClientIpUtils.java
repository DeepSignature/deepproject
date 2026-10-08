package com.deepprotech.deepproject.common.logging;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIpUtils {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";

    private ClientIpUtils() {
    }

    public static String resolve(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int comma = forwardedFor.indexOf(',');
            return (comma > 0 ? forwardedFor.substring(0, comma) : forwardedFor).trim();
        }

        String realIp = request.getHeader(X_REAL_IP);
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}
