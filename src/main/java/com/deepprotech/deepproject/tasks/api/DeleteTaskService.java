package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.tasks.commands.DeleteTaskCommand;

public interface DeleteTaskService {
    void handle(DeleteTaskCommand command);
}
