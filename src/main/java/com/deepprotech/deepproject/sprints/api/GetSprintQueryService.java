package com.deepprotech.deepproject.sprints.api;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.queries.GetSprintByIdQuery;
import com.deepprotech.deepproject.sprints.queries.ListSprintsByProjectQuery;

public interface GetSprintQueryService {
    Sprint handle(GetSprintByIdQuery query);
    CursorPage<Sprint> handle(ListSprintsByProjectQuery query);
}
