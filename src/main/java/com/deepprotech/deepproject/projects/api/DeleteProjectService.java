package com.deepprotech.deepproject.projects.api;

import com.deepprotech.deepproject.projects.commands.DeleteProjectCommand;

public interface DeleteProjectService {
    void handle(DeleteProjectCommand command);
}
