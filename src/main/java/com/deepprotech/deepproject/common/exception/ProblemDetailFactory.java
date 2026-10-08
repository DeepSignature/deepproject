package com.deepprotech.deepproject.common.exception;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;

@Component
public class ProblemDetailFactory {

    private final String typeBase;

    public ProblemDetailFactory(
            @Value("${app.error.type-base:https://api.deepproject.com/errors}") String typeBase) {
        this.typeBase = typeBase;
    }

    public ProblemDetail of(HttpStatus status, String title, String type, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(typeBase + "/" + type));
        pd.setProperty("timestamp", Instant.now().toString());
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            pd.setProperty("traceId", traceId);
        }
        return pd;
    }

    public ProblemDetail internalServerError(String detail) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "internal", detail);
    }

    public ProblemDetail badRequest(String detail) {
        return of(HttpStatus.BAD_REQUEST, "Bad Request", "bad-request", detail);
    }

    public ProblemDetail notFound(String detail) {
        return of(HttpStatus.NOT_FOUND, "Not Found", "not-found", detail);
    }

    public ProblemDetail unauthorized(String detail) {
        return of(HttpStatus.UNAUTHORIZED, "Unauthorized", "unauthorized", detail);
    }

    public ProblemDetail forbidden(String detail) {
        return of(HttpStatus.FORBIDDEN, "Forbidden", "forbidden", detail);
    }

    public ProblemDetail conflict(String detail) {
        return of(HttpStatus.CONFLICT, "Conflict", "conflict", detail);
    }
}
