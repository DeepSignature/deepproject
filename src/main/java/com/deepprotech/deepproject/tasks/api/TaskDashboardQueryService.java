package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.tasks.dto.AssigneeDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectDashboardResponse;
import com.deepprotech.deepproject.tasks.queries.GetAssigneeDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectDashboardQuery;

public interface TaskDashboardQueryService {
    ProjectDashboardResponse getDashboard(GetProjectDashboardQuery query);
    AssigneeDashboardResponse getAssigneeDashboard(GetAssigneeDashboardQuery query);
}
