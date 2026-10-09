package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.tasks.dto.AssigneeDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectAttentionResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectDashboardResponse;
import com.deepprotech.deepproject.tasks.dto.ProjectStatisticsResponse;
import com.deepprotech.deepproject.tasks.queries.GetAssigneeDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectAttentionQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectDashboardQuery;
import com.deepprotech.deepproject.tasks.queries.GetProjectStatisticsQuery;

public interface TaskDashboardQueryService {
    ProjectDashboardResponse getDashboard(GetProjectDashboardQuery query);
    AssigneeDashboardResponse getAssigneeDashboard(GetAssigneeDashboardQuery query);
    ProjectStatisticsResponse getStatistics(GetProjectStatisticsQuery query);
    ProjectAttentionResponse getAttention(GetProjectAttentionQuery query);
}
