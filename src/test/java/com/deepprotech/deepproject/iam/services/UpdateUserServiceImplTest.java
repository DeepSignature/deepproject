package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.commands.UpdateUserCommand;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceImplTest {

    @Mock UserRepository userRepository;
    @InjectMocks UpdateUserServiceImpl service;

    @Test
    void updatesUserAndReturnsIt() {
        UpdateUserCommand cmd = new UpdateUserCommand(1L, "New Name");
        User user = User.builder().id(1L).displayName("Old Name").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.handle(cmd);

        assertThat(result.getDisplayName()).isEqualTo("New Name");
        verify(userRepository).save(user);
    }

    @Test
    void throwsWhenUserNotFound() {
        UpdateUserCommand cmd = new UpdateUserCommand(999L, "Name");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
