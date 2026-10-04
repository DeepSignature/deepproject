package com.deepprotech.deepproject.projects.api;

import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.ChangeProjectStatusCommand;

public interface ChangeProjectStatusService {
    Project handle(ChangeProjectStatusCommand command);
}
