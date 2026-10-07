package com.deepprotech.deepproject.projects.web;

import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.ChangeProjectStatusService;
import com.deepprotech.deepproject.projects.api.CreateProjectService;
import com.deepprotech.deepproject.projects.api.DeleteProjectService;
import com.deepprotech.deepproject.projects.api.GetProjectQueryService;
import com.deepprotech.deepproject.projects.api.UpdateProjectService;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");
    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");

    @Mock CreateProjectService createProjectService;
    @Mock UpdateProjectService updateProjectService;
    @Mock ChangeProjectStatusService changeProjectStatusService;
    @Mock DeleteProjectService deleteProjectService;
    @Mock GetProjectQueryService getProjectQueryService;

    private MockMvc mockMvc;

    private final Project project = Project.builder().id(PROJECT_ID).workspaceId(WS_ID).name("Project").description("Desc").status("ACTIVE").build();

    @BeforeEach
    void setUp() {
        ProjectController controller = new ProjectController(createProjectService, updateProjectService,
                changeProjectStatusService, deleteProjectService, getProjectQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listReturnsProjects() throws Exception {
        when(getProjectQueryService.handle(any(ListProjectsByWorkspaceQuery.class))).thenReturn(List.of(project));
        mockMvc.perform(get("/api/workspaces/{workspaceId}/projects", WS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(PROJECT_ID.toString()));
    }

    @Test
    void getReturnsProject() throws Exception {
        when(getProjectQueryService.handle(any(GetProjectByIdQuery.class))).thenReturn(project);
        mockMvc.perform(get("/api/workspaces/{workspaceId}/projects/{id}", WS_ID, PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project"));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createProjectService.handle(any())).thenReturn(project);
        mockMvc.perform(post("/api/workspaces/{workspaceId}/projects", WS_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Project\",\"description\":\"Desc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Project"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Project updated = Project.builder().id(PROJECT_ID).workspaceId(WS_ID).name("Updated").description("Desc").status("ACTIVE").build();
        when(updateProjectService.handle(any())).thenReturn(updated);
        mockMvc.perform(put("/api/workspaces/{workspaceId}/projects/{id}", WS_ID, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"Desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void updateStatusReturnsOk() throws Exception {
        Project updated = Project.builder().id(PROJECT_ID).workspaceId(WS_ID).name("Project").description("Desc").status("COMPLETED").build();
        when(changeProjectStatusService.handle(any())).thenReturn(updated);
        mockMvc.perform(patch("/api/workspaces/{workspaceId}/projects/{id}/status?status=COMPLETED", WS_ID, PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/workspaces/{workspaceId}/projects/{id}", WS_ID, PROJECT_ID))
                .andExpect(status().isNoContent());
        verify(deleteProjectService).handle(any());
    }
}
