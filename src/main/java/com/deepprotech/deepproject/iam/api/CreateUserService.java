package com.deepprotech.deepproject.iam.api;

import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.commands.CreateUserCommand;

public interface CreateUserService {
    User handle(CreateUserCommand command);
}
