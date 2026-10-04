package com.deepprotech.deepproject.iam.web;

import com.deepprotech.deepproject.common.security.KeycloakJwtAuthenticationConverter;
import com.deepprotech.deepproject.common.security.AuthenticatedUserPrincipal;
import com.deepprotech.deepproject.common.security.SecurityUtils;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.CreateUserService;
import com.deepprotech.deepproject.iam.api.DeactivateUserService;
import com.deepprotech.deepproject.iam.api.GetMeQueryService;
import com.deepprotech.deepproject.iam.api.GetUserQueryService;
import com.deepprotech.deepproject.iam.api.UpdateUserService;
import com.deepprotech.deepproject.iam.commands.CreateUserCommand;
import com.deepprotech.deepproject.iam.commands.DeactivateUserCommand;
import com.deepprotech.deepproject.iam.commands.UpdateUserCommand;
import com.deepprotech.deepproject.iam.dto.CreateUserRequest;
import com.deepprotech.deepproject.iam.dto.UpdateUserRequest;
import com.deepprotech.deepproject.iam.dto.UserProfileResponse;
import com.deepprotech.deepproject.iam.dto.UserResponse;
import com.deepprotech.deepproject.iam.queries.GetMeQuery;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.ListUsersQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserService createUserService;
    private final UpdateUserService updateUserService;
    private final DeactivateUserService deactivateUserService;
    private final GetUserQueryService getUserQueryService;
    private final GetMeQueryService getMeQueryService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me() {
        AuthenticatedUserPrincipal principal = SecurityUtils.currentPrincipal()
                .orElseThrow(() -> new RuntimeException("Not authenticated"));
        UserProfileResponse profile = getMeQueryService.handle(
                new GetMeQuery(principal.identityId(), principal.roles()));
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<UserResponse>> list() {
        List<UserResponse> users = getUserQueryService.handle(new ListUsersQuery()).stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or @authz.isSelf(#id)")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        User user = getUserQueryService.handle(new GetUserByIdQuery(id));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User user = createUserService.handle(new CreateUserCommand(
                request.identityId(), request.username(), request.email(), request.displayName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or @authz.isSelf(#id)")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        User user = updateUserService.handle(new UpdateUserCommand(id, request.displayName()));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        deactivateUserService.handle(new DeactivateUserCommand(id));
        return ResponseEntity.noContent().build();
    }
}