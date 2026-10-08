package com.deepprotech.deepproject.common.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class AccessLogFilterTest {

    private final AccessLogFilter filter =
            new AccessLogFilter(true, "/actuator/health,/actuator/info");

    @Test
    void setsAndClearsMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("X-Forwarded-For", "203.0.113.10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> ipDuringChain = new AtomicReference<>();
        FilterChain chain = (req, res) -> ipDuringChain.set(MDC.get("clientIp"));

        filter.doFilter(request, response, chain);

        assertThat(ipDuringChain.get()).isEqualTo("203.0.113.10");
        assertThat(MDC.get("clientIp")).isNull();
        assertThat(MDC.get("user")).isNull();
        assertThat(MDC.get("method")).isNull();
        assertThat(MDC.get("uri")).isNull();
    }

    @Test
    void skipsExcludedPaths() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<Boolean> called = new AtomicReference<>(false);
        FilterChain chain = (req, res) -> called.set(true);

        filter.doFilter(request, response, chain);

        assertThat(called.get()).isTrue();
        assertThat(MDC.get("clientIp")).isNull();
    }

    @Test
    void disabledFilterPassesThrough() throws Exception {
        AccessLogFilter disabled = new AccessLogFilter(false, "");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> ipDuringChain = new AtomicReference<>();
        FilterChain chain = (req, res) -> ipDuringChain.set(MDC.get("clientIp"));

        disabled.doFilter(request, response, chain);

        assertThat(ipDuringChain.get()).isNull();
    }
}
