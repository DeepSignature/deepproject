package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.tasks.commands.ChangeTaskStatusCommand;

public interface ChangeTaskStatusService {
    void handle(ChangeTaskStatusCommand command);
}
