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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceImplTest {

    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock UserRepository userRepository;
    @InjectMocks UpdateUserServiceImpl service;

    @Test
    void updatesUserAndReturnsIt() {
        UpdateUserCommand cmd = new UpdateUserCommand(USER_ID, "New Name");
        User user = User.builder().id(USER_ID).displayName("Old Name").build();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.handle(cmd);

        assertThat(result.getDisplayName()).isEqualTo("New Name");
        verify(userRepository).save(user);
    }

    @Test
    void throwsWhenUserNotFound() {
        UpdateUserCommand cmd = new UpdateUserCommand(UNKNOWN_ID, "Name");
        when(userRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
