package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.commands.CreateUserCommand;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceImplTest {

    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Mock UserRepository userRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateUserServiceImpl service;

    @Test
    void createsUserAndPublishesEvent() {
        CreateUserCommand cmd = new CreateUserCommand("sub-1", "john", "john@example.com", "John Doe");
        User user = User.builder().id(USER_ID).identityId("sub-1").username("john").email("john@example.com").displayName("John Doe").active(true).build();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo("john");
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
