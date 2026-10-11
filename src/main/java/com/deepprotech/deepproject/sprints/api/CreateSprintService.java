package com.deepprotech.deepproject.sprints.api;

import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.commands.CreateSprintCommand;

public interface CreateSprintService {
    Sprint handle(CreateSprintCommand command);
}
