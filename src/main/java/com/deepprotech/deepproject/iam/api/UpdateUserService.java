package com.deepprotech.deepproject.iam.api;

import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.commands.UpdateUserCommand;

public interface UpdateUserService {
    User handle(UpdateUserCommand command);
}
