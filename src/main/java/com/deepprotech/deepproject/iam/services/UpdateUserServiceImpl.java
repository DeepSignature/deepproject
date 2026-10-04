package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.UpdateUserService;
import com.deepprotech.deepproject.iam.commands.UpdateUserCommand;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserServiceImpl implements UpdateUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User handle(UpdateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.userId()));

        if (command.displayName() != null) {
            user.setDisplayName(command.displayName());
        }

        User updated = userRepository.save(user);
        log.info("user_updated id={}", command.userId());
        return updated;
    }
}
