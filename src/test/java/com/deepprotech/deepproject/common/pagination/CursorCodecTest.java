package com.deepprotech.deepproject.common.pagination;

import com.deepprotech.deepproject.common.exception.InvalidCursorException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CursorCodecTest {

    private static final UUID ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");

    @Test
    void encodesAndDecodesRoundTrip() {
        Instant createdAt = Instant.parse("2026-10-08T09:00:00.123456Z");
        CursorKey key = new CursorKey(createdAt, ID);

        String encoded = CursorCodec.encode(key);
        CursorKey decoded = CursorCodec.decode(encoded);

        assertThat(decoded).isEqualTo(key);
    }

    @Test
    void decodeOrNullReturnsNullForNullCursor() {
        assertThat(CursorCodec.decodeOrNull(null)).isNull();
    }

    @Test
    void decodeThrowsForGarbageInput() {
        assertThatThrownBy(() -> CursorCodec.decode("!!!not-base64!!!"))
                .isInstanceOf(InvalidCursorException.class);
    }

    @Test
    void decodeThrowsForMissingSeparator() {
        String encoded = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("only-created-at".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertThatThrownBy(() -> CursorCodec.decode(encoded))
                .isInstanceOf(InvalidCursorException.class);
    }
}
