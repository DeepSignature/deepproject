package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Role;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.GetUserQueryService;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByIdentityIdQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByUsernameQuery;
import com.deepprotech.deepproject.iam.queries.ListUsersQuery;
import com.deepprotech.deepproject.iam.repository.RoleRepository;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserQueryServiceImpl implements GetUserQueryService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", query.userId()));
    }

    @Override
    public User handle(GetUserByUsernameQuery query) {
        return userRepository.findByUsername(query.username())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + query.username()));
    }

    @Override
    public User handle(GetUserByIdentityIdQuery query) {
        return userRepository.findByIdentityId(query.identityId()).orElse(null);
    }

    @Override
    public List<User> handle(ListUsersQuery query) {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public List<Role> getUserRoles(UUID userId) {
        return roleRepository.findRolesByUserId(userId);
    }
}
