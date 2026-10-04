package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.CreateTaskCommand;

public interface CreateTaskService {
    Task handle(CreateTaskCommand command);
}
