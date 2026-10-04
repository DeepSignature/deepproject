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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeactivateUserServiceImplTest {

    @Mock UserRepository userRepository;
    @InjectMocks DeactivateUserServiceImpl service;

    @Test
    void deactivatesUser() {
        User user = User.builder().id(1L).active(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        service.handle(new DeactivateUserCommand(1L));
        verify(userRepository).save(user);
    }
}
