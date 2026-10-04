package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;

public interface ChangeTaskStatusService {
    Task handle(ChangeTaskStatusCommand command);
}
