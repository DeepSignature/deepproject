package com.deepprotech.deepproject.common.dto;

import java.util.List;
import java.util.function.Function;

public record CursorPage<T>(List<T> items, String nextCursor, boolean hasMore) {

    public static <T> CursorPage<T> of(List<T> items, String nextCursor, boolean hasMore) {
        return new CursorPage<>(List.copyOf(items), nextCursor, hasMore);
    }

    public <R> CursorPage<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mapped = items.stream().<R>map(mapper).toList();
        return new CursorPage<>(mapped, nextCursor, hasMore);
    }
}
