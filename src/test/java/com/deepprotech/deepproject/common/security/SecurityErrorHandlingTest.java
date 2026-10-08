package com.deepprotech.deepproject.common.security;

import com.deepprotech.deepproject.common.exception.ProblemDetailFactory;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityErrorHandlingTest {

    @Test
    void authenticationEntryPointWrites401ProblemDetail() throws Exception {
        RestAuthenticationEntryPoint entryPoint =
                new RestAuthenticationEntryPoint(new SecurityErrorWriter(),
                        new ProblemDetailFactory("https://api.deepproject.com/errors"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(new MockHttpServletRequest(), response,
                new AuthenticationException("no token") {
                });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/problem+json");
        assertThat(response.getContentAsString()).contains("\"status\":401", "\"Unauthorized\"");
    }

    @Test
    void accessDeniedHandlerWrites403ProblemDetail() throws Exception {
        RestAccessDeniedHandler deniedHandler =
                new RestAccessDeniedHandler(new SecurityErrorWriter(),
                        new ProblemDetailFactory("https://api.deepproject.com/errors"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        deniedHandler.handle(new MockHttpServletRequest(), response,
                new AccessDeniedException("denied"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo("application/problem+json");
        assertThat(response.getContentAsString()).contains("\"status\":403", "\"Forbidden\"");
    }
}
