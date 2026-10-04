package com.deepprotech.deepproject.projects.api;

import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.CreateProjectCommand;

public interface CreateProjectService {
    Project handle(CreateProjectCommand command);
}
