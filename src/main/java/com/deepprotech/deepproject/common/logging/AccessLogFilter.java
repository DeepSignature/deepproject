package com.deepprotech.deepproject.common.logging;

import com.deepprotech.deepproject.common.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Order(0)
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("http.access");

    private static final String MDC_CLIENT_IP = "clientIp";
    private static final String MDC_USER = "user";
    private static final String MDC_METHOD = "method";
    private static final String MDC_URI = "uri";

    private final boolean enabled;
    private final Set<String> excludePaths;

    public AccessLogFilter(
            @Value("${app.logging.access.enabled:true}") boolean enabled,
            @Value("${app.logging.access.exclude-paths:}") String excludePaths) {
        this.enabled = enabled;
        this.excludePaths = new HashSet<>();
        if (excludePaths != null) {
            Arrays.stream(excludePaths.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(this.excludePaths::add);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!enabled || isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.nanoTime();
        String method = request.getMethod();
        String uri = request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        String ip = ClientIpUtils.resolve(request);
        String user = SecurityUtils.currentUsername().orElse("anonymous");

        MDC.put(MDC_CLIENT_IP, ip);
        MDC.put(MDC_USER, user);
        MDC.put(MDC_METHOD, method);
        MDC.put(MDC_URI, uri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            ACCESS_LOG.info("HTTP {} {} -> {} in {}ms ip={} user={}",
                    method, uri, response.getStatus(), durationMs, ip, user);
            MDC.remove(MDC_CLIENT_IP);
            MDC.remove(MDC_USER);
            MDC.remove(MDC_METHOD);
            MDC.remove(MDC_URI);
        }
    }

    private boolean isExcluded(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excludePaths.stream().anyMatch(path::startsWith);
    }
}
