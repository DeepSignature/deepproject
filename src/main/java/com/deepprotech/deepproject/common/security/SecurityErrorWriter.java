package com.deepprotech.deepproject.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityErrorWriter {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class);

    public void write(HttpServletResponse response, HttpStatus status, ProblemDetail problemDetail)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}
