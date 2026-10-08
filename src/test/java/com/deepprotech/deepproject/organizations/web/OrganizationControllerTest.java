package com.deepprotech.deepproject.organizations.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Organization;
import com.deepprotech.deepproject.core.OrganizationMember;
import com.deepprotech.deepproject.organizations.api.CreateOrganizationService;
import com.deepprotech.deepproject.organizations.api.DeleteOrganizationService;
import com.deepprotech.deepproject.organizations.api.GetOrganizationQueryService;
import com.deepprotech.deepproject.organizations.api.ManageOrganizationMemberService;
import com.deepprotech.deepproject.organizations.api.UpdateOrganizationService;
import com.deepprotech.deepproject.organizations.commands.AddOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.RemoveOrganizationMemberCommand;
import com.deepprotech.deepproject.organizations.commands.UpdateOrganizationMemberRoleCommand;
import com.deepprotech.deepproject.organizations.queries.GetOrganizationByIdQuery;
import com.deepprotech.deepproject.organizations.queries.PageOrganizationMembersQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    private static final UUID ORG_ID = UUID.fromString("a0000003-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID USER_ID_2 = UUID.fromString("a0000002-0000-0000-0000-000000000002");
    private static final UUID MEMBER_ID = UUID.fromString("a0000005-0000-0000-0000-000000000001");

    @Mock CreateOrganizationService createOrganizationService;
    @Mock UpdateOrganizationService updateOrganizationService;
    @Mock DeleteOrganizationService deleteOrganizationService;
    @Mock GetOrganizationQueryService getOrganizationQueryService;
    @Mock ManageOrganizationMemberService manageOrganizationMemberService;

    private MockMvc mockMvc;

    private final Organization org = Organization.builder().id(ORG_ID).identifier("my-org").name("My Org").description("Desc").build();

    @BeforeEach
    void setUp() {
        OrganizationController controller = new OrganizationController(createOrganizationService, updateOrganizationService,
                deleteOrganizationService, getOrganizationQueryService, manageOrganizationMemberService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getReturnsOrg() throws Exception {
        when(getOrganizationQueryService.handle(any(GetOrganizationByIdQuery.class))).thenReturn(org);

        mockMvc.perform(get("/api/organizations/{id}", ORG_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ORG_ID.toString()));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createOrganizationService.handle(any())).thenReturn(org);

        mockMvc.perform(post("/api/organizations?userId=" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"my-org\",\"name\":\"My Org\",\"description\":\"Desc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier").value("my-org"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Organization updated = Organization.builder().id(ORG_ID).identifier("my-org").name("Updated").description("Desc").build();
        when(updateOrganizationService.handle(any())).thenReturn(updated);

        mockMvc.perform(put("/api/organizations/{id}", ORG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"Desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/organizations/{id}", ORG_ID))
                .andExpect(status().isNoContent());
        verify(deleteOrganizationService).handle(any());
    }

    @Test
    void membersReturnsList() throws Exception {
        OrganizationMember member = OrganizationMember.builder().id(MEMBER_ID).organizationId(ORG_ID).userId(USER_ID_2).role("MEMBER").build();
        when(getOrganizationQueryService.handle(any(PageOrganizationMembersQuery.class))).thenReturn(CursorPage.of(List.of(member), null, false));

        mockMvc.perform(get("/api/organizations/{id}/members", ORG_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].userId").value(USER_ID_2.toString()));
    }

    @Test
    void addMemberReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/organizations/{id}/members?userId=" + USER_ID_2 + "&role=MEMBER", ORG_ID))
                .andExpect(status().isCreated());
        verify(manageOrganizationMemberService).handle(any(AddOrganizationMemberCommand.class));
    }

    @Test
    void removeMemberReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/organizations/{id}/members/{userId}", ORG_ID, USER_ID_2))
                .andExpect(status().isNoContent());
        verify(manageOrganizationMemberService).handle(any(RemoveOrganizationMemberCommand.class));
    }

    @Test
    void updateMemberRoleReturnsOk() throws Exception {
        mockMvc.perform(put("/api/organizations/{id}/members/{userId}/role?role=ADMIN", ORG_ID, USER_ID_2))
                .andExpect(status().isOk());
        verify(manageOrganizationMemberService).handle(any(UpdateOrganizationMemberRoleCommand.class));
    }
}
