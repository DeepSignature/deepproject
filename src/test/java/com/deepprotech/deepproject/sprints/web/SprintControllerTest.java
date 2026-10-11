package com.deepprotech.deepproject.sprints.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.api.ChangeSprintStatusService;
import com.deepprotech.deepproject.sprints.api.CreateSprintService;
import com.deepprotech.deepproject.sprints.api.DeleteSprintService;
import com.deepprotech.deepproject.sprints.api.GetSprintQueryService;
import com.deepprotech.deepproject.sprints.api.UpdateSprintService;
import com.deepprotech.deepproject.sprints.queries.GetSprintByIdQuery;
import com.deepprotech.deepproject.sprints.queries.ListSprintsByProjectQuery;
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
class SprintControllerTest {

    private static final UUID SPRINT_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    @Mock CreateSprintService createSprintService;
    @Mock UpdateSprintService updateSprintService;
    @Mock ChangeSprintStatusService changeSprintStatusService;
    @Mock DeleteSprintService deleteSprintService;
    @Mock GetSprintQueryService getSprintQueryService;

    private MockMvc mockMvc;

    private final Sprint sprint = Sprint.builder().id(SPRINT_ID).projectId(PROJECT_ID).name("Sprint").description("Desc").active(false).status("PLANNED").build();

    @BeforeEach
    void setUp() {
        SprintController controller = new SprintController(createSprintService, updateSprintService,
                changeSprintStatusService, deleteSprintService, getSprintQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listReturnsSprints() throws Exception {
        when(getSprintQueryService.handle(any(ListSprintsByProjectQuery.class))).thenReturn(CursorPage.of(List.of(sprint), null, false));
        mockMvc.perform(get("/api/projects/{projectId}/sprints", PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(SPRINT_ID.toString()));
    }

    @Test
    void getReturnsSprint() throws Exception {
        when(getSprintQueryService.handle(any(GetSprintByIdQuery.class))).thenReturn(sprint);
        mockMvc.perform(get("/api/projects/{projectId}/sprints/{id}", PROJECT_ID, SPRINT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sprint"));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createSprintService.handle(any())).thenReturn(sprint);
        mockMvc.perform(post("/api/projects/{projectId}/sprints", PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sprint\",\"description\":\"Desc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sprint"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Sprint updated = Sprint.builder().id(SPRINT_ID).projectId(PROJECT_ID).name("Updated").description("Desc").active(false).status("PLANNED").build();
        when(updateSprintService.handle(any())).thenReturn(updated);
        mockMvc.perform(put("/api/projects/{projectId}/sprints/{id}", PROJECT_ID, SPRINT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"Desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void updateStatusReturnsOk() throws Exception {
        Sprint updated = Sprint.builder().id(SPRINT_ID).projectId(PROJECT_ID).name("Sprint").description("Desc").active(true).status("ACTIVE").build();
        when(changeSprintStatusService.handle(any())).thenReturn(updated);
        mockMvc.perform(patch("/api/projects/{projectId}/sprints/{id}/status?status=ACTIVE", PROJECT_ID, SPRINT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/sprints/{id}", PROJECT_ID, SPRINT_ID))
                .andExpect(status().isNoContent());
        verify(deleteSprintService).handle(any());
    }
}
