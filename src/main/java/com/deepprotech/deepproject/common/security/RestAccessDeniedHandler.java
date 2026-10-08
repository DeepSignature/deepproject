package com.deepprotech.deepproject.common.security;

import com.deepprotech.deepproject.common.exception.ProblemDetailFactory;
import com.deepprotech.deepproject.common.logging.ClientIpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorWriter errorWriter;
    private final ProblemDetailFactory problemDetailFactory;

    public RestAccessDeniedHandler(SecurityErrorWriter errorWriter,
                                   ProblemDetailFactory problemDetailFactory) {
        this.errorWriter = errorWriter;
        this.problemDetailFactory = problemDetailFactory;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("HTTP {} {} -> 403 ip={} user=anonymous",
                request.getMethod(), request.getRequestURI(), ClientIpUtils.resolve(request));
        errorWriter.write(response, HttpStatus.FORBIDDEN,
                problemDetailFactory.forbidden("Access denied"));
    }
}
