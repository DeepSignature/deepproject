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
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserQueryServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @InjectMocks GetUserQueryServiceImpl service;

    @Test
    void getByIdReturnsUser() {
        User user = User.builder().id(1L).username("john").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(service.handle(new GetUserByIdQuery(1L))).isEqualTo(user);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetUserByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByUsernameReturnsUser() {
        User user = User.builder().id(1L).username("john").build();
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
        User user = User.builder().id(1L).identityId("sub-1").build();
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
        User user = User.builder().id(1L).username("john").build();
        when(userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(user));
        assertThat(service.handle(new ListUsersQuery())).containsExactly(user);
    }

    @Test
    void getUserRolesReturnsRoles() {
        Role role = Role.builder().id(1L).name("ROLE_ADMIN").build();
        when(roleRepository.findRolesByUserId(1L)).thenReturn(List.of(role));
        assertThat(service.getUserRoles(1L)).containsExactly(role);
    }
}
