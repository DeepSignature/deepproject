package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.CreateUserService;
import com.deepprotech.deepproject.iam.commands.CreateUserCommand;
import com.deepprotech.deepproject.iam.events.UserCreatedEvent;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserServiceImpl implements CreateUserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public User handle(CreateUserCommand command) {
        User user = User.builder()
                .identityId(command.identityId())
                .username(command.username())
                .email(command.email())
                .displayName(command.displayName())
                .active(true)
                .build();

        user = userRepository.save(user);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getUsername(), user.getEmail(), Instant.now()));
        log.info("user_created id={} username={}", user.getId(), command.username());
        return user;
    }
}
