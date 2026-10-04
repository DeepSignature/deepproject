package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.UpdateTaskCommand;

public interface UpdateTaskService {
    Task handle(UpdateTaskCommand command);
}
