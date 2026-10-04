package com.deepprotech.deepproject.iam.api;

import com.deepprotech.deepproject.iam.commands.DeactivateUserCommand;

public interface DeactivateUserService {
    void handle(DeactivateUserCommand command);
}
