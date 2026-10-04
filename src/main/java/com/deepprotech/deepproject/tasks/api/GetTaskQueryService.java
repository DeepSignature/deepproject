package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.core.TaskAssignee;
import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.queries.*;

import java.util.List;

public interface GetTaskQueryService {
    Task handle(GetTaskByIdQuery query);
    List<Task> handle(ListTasksByProjectQuery query);
    List<Task> handle(ListSubtasksQuery query);
    List<TaskAssignee> handle(ListTaskAssigneesQuery query);
    List<TaskTag> handle(ListTaskTagsQuery query);
}
