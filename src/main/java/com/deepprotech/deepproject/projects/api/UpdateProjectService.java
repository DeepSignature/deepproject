package com.deepprotech.deepproject.projects.api;

import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.UpdateProjectCommand;

public interface UpdateProjectService {
    Project handle(UpdateProjectCommand command);
}
