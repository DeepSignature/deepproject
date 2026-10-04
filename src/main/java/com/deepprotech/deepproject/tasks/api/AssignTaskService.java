package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.tasks.commands.AssignTaskUserCommand;
import com.deepprotech.deepproject.tasks.commands.UnassignTaskUserCommand;

public interface AssignTaskService {
    void handle(AssignTaskUserCommand command);
    void handle(UnassignTaskUserCommand command);
}
