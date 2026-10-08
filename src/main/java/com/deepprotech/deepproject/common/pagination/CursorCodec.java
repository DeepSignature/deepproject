package com.deepprotech.deepproject.common.pagination;

import com.deepprotech.deepproject.common.exception.InvalidCursorException;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public final class CursorCodec {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private CursorCodec() {
    }

    public static String encode(CursorKey key) {
        String raw = key.createdAt() + "|" + key.id();
        return ENCODER.encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    public static CursorKey decodeOrNull(@Nullable String cursor) {
        return cursor == null ? null : decode(cursor);
    }

    public static CursorKey decode(String cursor) {
        try {
            String raw = new String(DECODER.decode(cursor), StandardCharsets.UTF_8);
            int separator = raw.indexOf('|');
            if (separator < 0) {
                throw new InvalidCursorException(cursor);
            }
            Instant createdAt = Instant.parse(raw.substring(0, separator));
            UUID id = UUID.fromString(raw.substring(separator + 1));
            return new CursorKey(createdAt, id);
        } catch (InvalidCursorException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            throw new InvalidCursorException(cursor);
        }
    }
}
