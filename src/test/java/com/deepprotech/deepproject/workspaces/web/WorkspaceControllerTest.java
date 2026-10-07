package com.deepprotech.deepproject.workspaces.web;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.api.CreateWorkspaceService;
import com.deepprotech.deepproject.workspaces.api.DeleteWorkspaceService;
import com.deepprotech.deepproject.workspaces.api.GetWorkspaceQueryService;
import com.deepprotech.deepproject.workspaces.api.UpdateWorkspaceService;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceByIdQuery;
import com.deepprotech.deepproject.workspaces.queries.ListUserWorkspacesQuery;
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
class WorkspaceControllerTest {

    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID ORG_ID = UUID.fromString("a0000003-0000-0000-0000-000000000001");

    @Mock CreateWorkspaceService createWorkspaceService;
    @Mock UpdateWorkspaceService updateWorkspaceService;
    @Mock DeleteWorkspaceService deleteWorkspaceService;
    @Mock GetWorkspaceQueryService getWorkspaceQueryService;

    private MockMvc mockMvc;

    private final Workspace ws = Workspace.builder().id(WS_ID).name("WS").slug("ws").description("D").ownerId(USER_ID).organizationId(ORG_ID).build();

    @BeforeEach
    void setUp() {
        WorkspaceController controller = new WorkspaceController(createWorkspaceService, updateWorkspaceService,
                deleteWorkspaceService, getWorkspaceQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listReturnsWorkspaces() throws Exception {
        when(getWorkspaceQueryService.handle(any(ListUserWorkspacesQuery.class))).thenReturn(List.of(ws));
        mockMvc.perform(get("/api/workspaces?userId=" + USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(WS_ID.toString()));
    }

    @Test
    void getReturnsWorkspace() throws Exception {
        when(getWorkspaceQueryService.handle(any(GetWorkspaceByIdQuery.class))).thenReturn(ws);
        mockMvc.perform(get("/api/workspaces/{id}", WS_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WS"));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createWorkspaceService.handle(any())).thenReturn(ws);
        mockMvc.perform(post("/api/workspaces?ownerId=" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"WS\",\"slug\":\"ws\",\"description\":\"D\",\"organizationId\":\"" + ORG_ID + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("WS"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Workspace updated = Workspace.builder().id(WS_ID).name("Updated").slug("ws").description("D").ownerId(USER_ID).build();
        when(updateWorkspaceService.handle(any())).thenReturn(updated);
        mockMvc.perform(put("/api/workspaces/{id}", WS_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"D\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/workspaces/{id}", WS_ID.toString()))
                .andExpect(status().isNoContent());
        verify(deleteWorkspaceService).handle(any());
    }
}
