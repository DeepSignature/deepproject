package com.deepprotech.deepproject.tasks.api;

import com.deepprotech.deepproject.tasks.commands.AddTaskTagCommand;
import com.deepprotech.deepproject.tasks.commands.RemoveTaskTagCommand;

public interface ManageTaskTagService {
    void handle(AddTaskTagCommand command);
    void handle(RemoveTaskTagCommand command);
}
