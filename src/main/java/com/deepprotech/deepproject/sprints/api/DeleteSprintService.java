package com.deepprotech.deepproject.sprints.api;

import com.deepprotech.deepproject.sprints.commands.DeleteSprintCommand;

public interface DeleteSprintService {
    void handle(DeleteSprintCommand command);
}
