package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.iam.api.DeactivateUserService;
import com.deepprotech.deepproject.iam.commands.DeactivateUserCommand;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivateUserServiceImpl implements DeactivateUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void handle(DeactivateUserCommand command) {
        userRepository.findById(command.userId()).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
        log.info("user_deactivated id={}", command.userId());
    }
}
