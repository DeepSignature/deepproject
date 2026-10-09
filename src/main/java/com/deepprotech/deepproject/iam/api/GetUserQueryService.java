package com.deepprotech.deepproject.iam.api;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Role;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByIdentityIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByUsernameQuery;
import com.deepprotech.deepproject.iam.queries.GetUsersByIdsQuery;
import com.deepprotech.deepproject.iam.queries.ListUsersQuery;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GetUserQueryService {
    User handle(GetUserByIdQuery query);
    User handle(GetUserByUsernameQuery query);
    User handle(GetUserByIdentityIdQuery query);
    Map<UUID, User> handle(GetUsersByIdsQuery query);
    CursorPage<User> handle(ListUsersQuery query);
    List<Role> getUserRoles(UUID userId);
    User getUserById(UUID userId);
}
