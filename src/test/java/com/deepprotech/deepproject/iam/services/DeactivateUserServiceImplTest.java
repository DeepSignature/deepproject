package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.commands.DeactivateUserCommand;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeactivateUserServiceImplTest {

    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Mock UserRepository userRepository;
    @InjectMocks DeactivateUserServiceImpl service;

    @Test
    void deactivatesUser() {
        User user = User.builder().id(USER_ID).active(true).build();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        service.handle(new DeactivateUserCommand(USER_ID));
        verify(userRepository).save(user);
    }
}
