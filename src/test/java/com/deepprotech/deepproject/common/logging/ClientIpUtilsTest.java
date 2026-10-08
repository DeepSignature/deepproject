package com.deepprotech.deepproject.common.logging;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ClientIpUtilsTest {

    @Test
    void resolvesFirstForwardedForValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.1");

        assertThat(ClientIpUtils.resolve(request)).isEqualTo("203.0.113.10");
    }

    @Test
    void resolvesRealIpWhenNoForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", "198.51.100.5");

        assertThat(ClientIpUtils.resolve(request)).isEqualTo("198.51.100.5");
    }

    @Test
    void fallsBackToRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.7");

        assertThat(ClientIpUtils.resolve(request)).isEqualTo("192.0.2.7");
    }

    @Test
    void skipsBlankForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", " ");
        request.setRemoteAddr("192.0.2.8");

        assertThat(ClientIpUtils.resolve(request)).isEqualTo("192.0.2.8");
    }
}
