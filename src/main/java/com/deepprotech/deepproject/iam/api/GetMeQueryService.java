package com.deepprotech.deepproject.iam.api;

import com.deepprotech.deepproject.iam.dto.UserProfileResponse;
import com.deepprotech.deepproject.iam.queries.GetMeQuery;

public interface GetMeQueryService {
    UserProfileResponse handle(GetMeQuery query);
}