package com.deepprotech.deepproject.organizations.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.organizations.api.CreateOrganizationService;
import com.deepprotech.deepproject.organizations.api.DeleteOrganizationService;
import com.deepprotech.deepproject.organizations.api.GetOrganizationQueryService;
import com.deepprotech.deepproject.organizations.api.ManageOrganizationMemberService;
import com.deepprotech.deepproject.organizations.api.UpdateOrganizationService;
import com.deepprotech.deepproject.organizations.commands.AddOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.CreateOrganizationCommand;
import com.deepprotech.deepproject.organizations.commands.DeleteOrganizationCommand;
import com.deepprotech.deepproject.organizations.commands.RemoveOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationMemberRoleCommand;
import com.deepprotech.deepproject.organizations.dto.CreateOrganizationRequest;
import com.deepprotech.deepproject.organizations.dto.OrganizationMemberResponse;
import com.deepprotech.deepproject.organizations.dto.OrganizationResponse;
import com.deepprotech.deepproject.organizations.dto.UpdateOrganizationRequest;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.PageOrganizationMembersQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final CreateOrganizationService createOrganizationService;
    private final UpdateOrganizationService updateOrganizationService;
    private final DeleteOrganizationService deleteOrganizationService;
    private final GetOrganizationQueryService getOrganizationQueryService;
    private final ManageOrganizationMemberService manageOrganizationMemberService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ORG_READ')")
    public ResponseEntity<OrganizationResponse> get(@PathVariable UUID id) {
        Organization org = getOrganizationQueryService.handle(new GetOrganizationByIdQuery(id));
        return ResponseEntity.ok(OrganizationResponse.from(org));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_ORG_CREATE')")
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody CreateOrganizationRequest request,
                                                       @RequestParam UUID userId) {
        Organization org = createOrganizationService.handle(new CreateOrganizationCommand(
                request.identifier(), request.name(), request.description(), userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(OrganizationResponse.from(org));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ORG_UPDATE')")
    public ResponseEntity<OrganizationResponse> update(@PathVariable UUID id,
                                                       @Valid @RequestBody UpdateOrganizationRequest request) {
        Organization org = updateOrganizationService.handle(new UpdateOrganizationCommand(id, request.name(), request.description()));
        return ResponseEntity.ok(OrganizationResponse.from(org));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ORG_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteOrganizationService.handle(new DeleteOrganizationCommand(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("hasAuthority('PERMISSION_ORG_READ')")
    public ResponseEntity<CursorPage<OrganizationMemberResponse>> members(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String cursor) {
        CursorPage<OrganizationMemberResponse> list = getOrganizationQueryService.handle(new PageOrganizationMembersQuery(id, limit, cursor))
                .map(OrganizationMemberResponse::from);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAuthority('PERMISSION_ORG_MEMBER_MANAGE')")
    public ResponseEntity<Void> addMember(@PathVariable UUID id, @RequestParam UUID userId, @RequestParam String role) {
        manageOrganizationMemberService.handle(new AddOrganizationMemberCommand(id, userId, role));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasAuthority('PERMISSION_ORG_MEMBER_MANAGE')")
    public ResponseEntity<Void> removeMember(@PathVariable UUID id, @PathVariable UUID userId) {
        manageOrganizationMemberService.handle(new RemoveOrganizationMemberCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/members/{userId}/role")
    @PreAuthorize("hasAuthority('PERMISSION_ORG_MEMBER_MANAGE')")
    public ResponseEntity<Void> updateMemberRole(@PathVariable UUID id, @PathVariable UUID userId, @RequestParam String role) {
        manageOrganizationMemberService.handle(new UpdateOrganizationMemberRoleCommand(id, userId, role));
        return ResponseEntity.ok().build();
    }
}