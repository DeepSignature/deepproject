package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.queries.*;

import java.util.List;

public interface GetTaskQueryService {
    Task handle(GetTaskByIdQuery query);
    CursorPage<Task> handle(ListTasksByProjectQuery query);
    CursorPage<Task> handle(ListSubtasksQuery query);
    List<TaskAssignee> handle(ListTaskAssigneesQuery query);
    List<TaskTag> handle(ListTaskTagsQuery query);
}
