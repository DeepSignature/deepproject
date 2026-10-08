package com.deepprotech.deepproject.common.pagination;

import com.deepprotech.deepproject.common.dto.CursorPage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class CursorPages {

    private CursorPages() {
    }

    public static <T> CursorPage<T> build(List<T> rows, int limit,
                                          Function<T, Instant> createdAt, Function<T, UUID> id) {
        boolean hasMore = rows.size() > limit;
        List<T> items = hasMore ? rows.subList(0, limit) : rows;
        String nextCursor = null;
        if (hasMore) {
            T last = items.get(items.size() - 1);
            nextCursor = CursorCodec.encode(new CursorKey(createdAt.apply(last), id.apply(last)));
        }
        return CursorPage.of(items, nextCursor, hasMore);
    }
}
