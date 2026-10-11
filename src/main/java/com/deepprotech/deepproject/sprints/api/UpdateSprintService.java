package com.deepprotech.deepproject.sprints.api;

import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.commands.UpdateSprintCommand;

public interface UpdateSprintService {
    Sprint handle(UpdateSprintCommand command);
}
