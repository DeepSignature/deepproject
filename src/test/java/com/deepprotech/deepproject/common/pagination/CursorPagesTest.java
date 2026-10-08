package com.deepprotech.deepproject.common.pagination;

import com.deepprotech.deepproject.common.dto.CursorPage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CursorPagesTest {

    private record Item(Instant createdAt, UUID id) {}

    @Test
    void buildsPageWithoutMoreWhenRowsUnderLimit() {
        Item item = new Item(Instant.now(), UUID.randomUUID());

        CursorPage<Item> page = CursorPages.build(List.of(item), 20, Item::createdAt, Item::id);

        assertThat(page.items()).containsExactly(item);
        assertThat(page.hasMore()).isFalse();
        assertThat(page.nextCursor()).isNull();
    }

    @Test
    void buildsPageWithMoreAndCursorWhenRowsExceedLimit() {
        Instant now = Instant.now();
        Item first = new Item(now, UUID.randomUUID());
        Item second = new Item(now.plusSeconds(1), UUID.randomUUID());

        CursorPage<Item> page = CursorPages.build(List.of(first, second), 1, Item::createdAt, Item::id);

        assertThat(page.items()).containsExactly(first);
        assertThat(page.hasMore()).isTrue();
        assertThat(CursorCodec.decode(page.nextCursor())).isEqualTo(new CursorKey(first.createdAt(), first.id()));
    }
}
