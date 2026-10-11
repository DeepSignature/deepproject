package com.deepprotech.deepproject.sprints.api;

import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.commands.ChangeSprintStatusCommand;

public interface ChangeSprintStatusService {
    Sprint handle(ChangeSprintStatusCommand command);
}
