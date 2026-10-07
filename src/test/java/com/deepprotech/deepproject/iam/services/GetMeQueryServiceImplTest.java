package com.deepprotech.deepproject.iam.services;

import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.dto.UserProfileResponse;
import com.deepprotech.deepproject.iam.queries.GetMeQuery;
import com.deepprotech.deepproject.iam.repository.UserRepository;
import com.deepprotech.deepproject.organizations.api.GetOrganizationQueryService;
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import com.deepprotech.deepproject.organizations.queries.ListUserOrganizationsQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMeQueryServiceImplTest {

    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID USER_ID_2 = UUID.fromString("a0000002-0000-0000-0000-000000000002");
    private static final UUID ORG_ID = UUID.fromString("a0000003-0000-0000-0000-000000000001");

    @Mock UserRepository userRepository;
    @Mock GetOrganizationQueryService getOrganizationQueryService;
    @InjectMocks GetMeQueryServiceImpl service;

    @Test
    void returnsProfileForExistingUser() {
        GetMeQuery query = new GetMeQuery("sub-1", List.of("SYSTEM_ADMIN"));
        User user = User.builder().id(USER_ID).identityId("sub-1").username("admin").email("admin@test.com").displayName("Admin").active(true).build();
        Organization org = Organization.builder().id(ORG_ID).identifier("acme").name("Acme").build();
        OrganizationMember member = OrganizationMember.builder().id(USER_ID).organizationId(ORG_ID).userId(USER_ID).role("ORGANIZATION_ADMIN").build();

        when(userRepository.findByIdentityId("sub-1")).thenReturn(Optional.of(user));
        when(getOrganizationQueryService.handle(any(ListUserOrganizationsQuery.class))).thenReturn(List.of(org));
        when(getOrganizationQueryService.handle(any(ListOrganizationMembersQuery.class))).thenReturn(List.of(member));

        UserProfileResponse response = service.handle(query);

        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.globalRoles()).contains("SYSTEM_ADMIN");
        assertThat(response.organizations()).hasSize(1);
        assertThat(response.organizations().get(0).role()).isEqualTo("ORGANIZATION_ADMIN");
    }

    @Test
    void provisionsNewUserWhenNotFound() {
        GetMeQuery query = new GetMeQuery("new-sub", List.of());
        User user = User.builder().id(USER_ID_2).identityId("new-sub").username("kc_new-sub").email("new-sub@placeholder").displayName("kc_new-sub").active(true).build();

        when(userRepository.findByIdentityId("new-sub")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(getOrganizationQueryService.handle(any(ListUserOrganizationsQuery.class))).thenReturn(List.of());

        UserProfileResponse response = service.handle(query);

        assertThat(response.userId()).isEqualTo(USER_ID_2);
        assertThat(response.identityId()).isEqualTo("new-sub");
    }
}
