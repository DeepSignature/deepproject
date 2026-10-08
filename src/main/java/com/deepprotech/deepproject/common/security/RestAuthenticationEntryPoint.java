package com.deepprotech.deepproject.common.security;

import com.deepprotech.deepproject.common.exception.ProblemDetailFactory;
import com.deepprotech.deepproject.common.logging.ClientIpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorWriter errorWriter;
    private final ProblemDetailFactory problemDetailFactory;

    public RestAuthenticationEntryPoint(SecurityErrorWriter errorWriter,
                                        ProblemDetailFactory problemDetailFactory) {
        this.errorWriter = errorWriter;
        this.problemDetailFactory = problemDetailFactory;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("HTTP {} {} -> 401 ip={} user=anonymous",
                request.getMethod(), request.getRequestURI(), ClientIpUtils.resolve(request));
        errorWriter.write(response, HttpStatus.UNAUTHORIZED,
                problemDetailFactory.unauthorized("Authentication required"));
    }
}
