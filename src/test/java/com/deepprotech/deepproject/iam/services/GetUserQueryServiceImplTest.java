package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Role;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByIdentityIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByUsernameQuery;
import com.deepprotech.deepproject.iam.queries.ListUsersQuery;
import com.deepprotech.deepproject.iam.repository.RoleRepository;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserQueryServiceImplTest {

    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID ROLE_ID = UUID.fromString("a0000004-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-0000-0000-0000-000000000001");

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @InjectMocks GetUserQueryServiceImpl service;

    @Test
    void getByIdReturnsUser() {
        User user = User.builder().id(USER_ID).username("john").build();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        assertThat(service.handle(new GetUserByIdQuery(USER_ID))).isEqualTo(user);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(userRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetUserByIdQuery(UNKNOWN_ID))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByUsernameReturnsUser() {
        User user = User.builder().id(USER_ID).username("john").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        assertThat(service.handle(new GetUserByUsernameQuery("john"))).isEqualTo(user);
    }

    @Test
    void getByUsernameThrowsWhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetUserByUsernameQuery("unknown"))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByIdentityIdReturnsUser() {
        User user = User.builder().id(USER_ID).identityId("sub-1").build();
        when(userRepository.findByIdentityId("sub-1")).thenReturn(Optional.of(user));
        assertThat(service.handle(new GetUserByIdentityIdQuery("sub-1"))).isEqualTo(user);
    }

    @Test
    void getByIdentityIdReturnsNullWhenNotFound() {
        when(userRepository.findByIdentityId("unknown")).thenReturn(Optional.empty());
        assertThat(service.handle(new GetUserByIdentityIdQuery("unknown"))).isNull();
    }

    @Test
    void listUsersReturnsAll() {
        User user = User.builder().id(USER_ID).username("john").build();
        when(userRepository.findAllOrderByCreatedAtAscIdAsc(any(Pageable.class))).thenReturn(List.of(user));
        assertThat(service.handle(new ListUsersQuery(20, null)).items()).containsExactly(user);
    }

    @Test
    void getUserRolesReturnsRoles() {
        Role role = Role.builder().id(ROLE_ID).name("ROLE_ADMIN").build();
        when(roleRepository.findRolesByUserId(USER_ID)).thenReturn(List.of(role));
        assertThat(service.getUserRoles(USER_ID)).containsExactly(role);
    }
}
