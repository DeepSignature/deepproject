package com.deepprotech.deepproject.iam.queries;

import org.springframework.lang.Nullable;

public record ListUsersQuery(int limit, @Nullable String cursor) {}
