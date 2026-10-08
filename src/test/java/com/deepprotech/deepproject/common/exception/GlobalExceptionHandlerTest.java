package com.deepprotech.deepproject.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private Environment environment;
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        environment = mock(Environment.class);
        handler = new GlobalExceptionHandler(environment,
                new ProblemDetailFactory("https://api.deepproject.com/errors"));
    }

    @Test
    void genericExceptionReturns500WithStacktraceInNonProd() {
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true);

        ProblemDetail pd = handler.handleGeneric(new IllegalStateException("boom"));

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(pd.getTitle()).isEqualTo("Internal Server Error");
        assertThat((String) pd.getProperties().get("stackTrace")).contains("IllegalStateException");
    }

    @Test
    void genericExceptionReturns500WithoutStacktraceInProd() {
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(false);

        ProblemDetail pd = handler.handleGeneric(new IllegalStateException("boom"));

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(pd.getProperties()).doesNotContainKey("stackTrace");
    }

    @Test
    void resourceNotFoundReturns404() {
        ProblemDetail pd = handler.handleNotFound(new ResourceNotFoundException("Task", 42));

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getDetail()).isEqualTo("Task not found with id: 42");
    }

    @Test
    void invalidCursorReturns400() {
        ProblemDetail pd = handler.handleInvalidCursor(new InvalidCursorException("abc"));

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void illegalArgumentReturns400() {
        ProblemDetail pd = handler.handleBadRequest(new IllegalArgumentException("limit must be positive"));

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void validationReturns400WithFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail pd = handler.handleValidation(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getProperties().get("fieldErrors")).asString().contains("email");
    }

    @Test
    void constraintViolationReturns400() {
        ConstraintViolationException ex = new ConstraintViolationException("invalid", Collections.emptySet());

        ProblemDetail pd = handler.handleConstraintViolation(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void accessDeniedReturns403() {
        ProblemDetail pd = handler.handleAccessDenied(new AccessDeniedException("nope"));

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void dataIntegrityReturns409() {
        ProblemDetail pd = handler.handleDataIntegrity(new DataIntegrityViolationException("unique violation"));

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
