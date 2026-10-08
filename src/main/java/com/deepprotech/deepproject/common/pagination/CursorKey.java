package com.deepprotech.deepproject.common.pagination;

import java.time.Instant;
import java.util.UUID;

public record CursorKey(Instant createdAt, UUID id) {}
