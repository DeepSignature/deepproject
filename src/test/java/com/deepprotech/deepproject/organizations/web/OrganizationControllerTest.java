package com.deepprotech.deepproject.organizations.web;

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
import com.deepprotech.deepproject.organizations.queries.ListOrganizationMembersQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

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

    @Mock CreateOrganizationService createOrganizationService;
    @Mock UpdateOrganizationService updateOrganizationService;
    @Mock DeleteOrganizationService deleteOrganizationService;
    @Mock GetOrganizationQueryService getOrganizationQueryService;
    @Mock ManageOrganizationMemberService manageOrganizationMemberService;

    private MockMvc mockMvc;

    private final Organization org = Organization.builder().id(1L).identifier("my-org").name("My Org").description("Desc").build();

    @BeforeEach
    void setUp() {
        OrganizationController controller = new OrganizationController(createOrganizationService, updateOrganizationService,
                deleteOrganizationService, getOrganizationQueryService, manageOrganizationMemberService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getReturnsOrg() throws Exception {
        when(getOrganizationQueryService.handle(any(GetOrganizationByIdQuery.class))).thenReturn(org);

        mockMvc.perform(get("/api/organizations/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createOrganizationService.handle(any())).thenReturn(org);

        mockMvc.perform(post("/api/organizations?userId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"my-org\",\"name\":\"My Org\",\"description\":\"Desc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier").value("my-org"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Organization updated = Organization.builder().id(1L).identifier("my-org").name("Updated").description("Desc").build();
        when(updateOrganizationService.handle(any())).thenReturn(updated);

        mockMvc.perform(put("/api/organizations/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"Desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/organizations/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(deleteOrganizationService).handle(any());
    }

    @Test
    void membersReturnsList() throws Exception {
        OrganizationMember member = OrganizationMember.builder().id(1L).organizationId(1L).userId(2L).role("MEMBER").build();
        when(getOrganizationQueryService.handle(any(ListOrganizationMembersQuery.class))).thenReturn(List.of(member));

        mockMvc.perform(get("/api/organizations/{id}/members", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
    }

    @Test
    void addMemberReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/organizations/{id}/members?userId=2&role=MEMBER", 1L))
                .andExpect(status().isCreated());
        verify(manageOrganizationMemberService).handle(any(AddOrganizationMemberCommand.class));
    }

    @Test
    void removeMemberReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/organizations/{id}/members/{userId}", 1L, 2L))
                .andExpect(status().isNoContent());
        verify(manageOrganizationMemberService).handle(any(RemoveOrganizationMemberCommand.class));
    }

    @Test
    void updateMemberRoleReturnsOk() throws Exception {
        mockMvc.perform(put("/api/organizations/{id}/members/{userId}/role?role=ADMIN", 1L, 2L))
                .andExpect(status().isOk());
        verify(manageOrganizationMemberService).handle(any(UpdateOrganizationMemberRoleCommand.class));
    }
}
