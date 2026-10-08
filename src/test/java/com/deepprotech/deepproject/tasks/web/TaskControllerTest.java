package com.deepprotech.deepproject.tasks.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Task;
import com.deepprotech.deepproject.tasks.api.AssignTaskService;
import com.deepprotech.deepproject.tasks.api.ChangeTaskStatusService;
import com.deepprotech.deepproject.tasks.api.CreateTaskService;
import com.deepprotech.deepproject.tasks.api.DeleteTaskService;
import com.deepprotech.deepproject.tasks.api.GetTaskQueryService;
import com.deepprotech.deepproject.tasks.api.ManageTaskTagService;
import com.deepprotech.deepproject.tasks.api.UpdateTaskService;
import com.deepprotech.deepproject.tasks.commands.AddTaskTagCommand;
import com.deepprotech.deepproject.tasks.commands.AssignTaskUserCommand;
import com.deepprotech.deepproject.tasks.commands.RemoveTaskTagCommand;
import com.deepprotech.deepproject.tasks.commands.UnassignTaskUserCommand;
import com.deepprotech.deepproject.tasks.queries.GetTaskByIdQuery;
import com.deepprotech.deepproject.tasks.queries.ListSubtasksQuery;
import com.deepprotech.deepproject.tasks.queries.ListTasksByProjectQuery;
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
class TaskControllerTest {

    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");
    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Mock CreateTaskService createTaskService;
    @Mock UpdateTaskService updateTaskService;
    @Mock ChangeTaskStatusService changeTaskStatusService;
    @Mock AssignTaskService assignTaskService;
    @Mock ManageTaskTagService manageTaskTagService;
    @Mock DeleteTaskService deleteTaskService;
    @Mock GetTaskQueryService getTaskQueryService;

    private MockMvc mockMvc;

    private final Task task = Task.builder().id(TASK_ID).projectId(PROJECT_ID).title("Task").description("Desc").status("TODO").priority("HIGH").taskType("TASK").build();

    @BeforeEach
    void setUp() {
        TaskController controller = new TaskController(createTaskService, updateTaskService, changeTaskStatusService,
                assignTaskService, manageTaskTagService, deleteTaskService, getTaskQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listReturnsTasks() throws Exception {
        when(getTaskQueryService.handle(any(ListTasksByProjectQuery.class))).thenReturn(CursorPage.of(List.of(task), null, false));
        mockMvc.perform(get("/api/projects/{projectId}/tasks", PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(TASK_ID.toString()));
    }

    @Test
    void getReturnsTask() throws Exception {
        when(getTaskQueryService.handle(any(GetTaskByIdQuery.class))).thenReturn(task);
        mockMvc.perform(get("/api/projects/{projectId}/tasks/{id}", PROJECT_ID, TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task"));
    }

    @Test
    void subtasksReturnsList() throws Exception {
        when(getTaskQueryService.handle(any(ListSubtasksQuery.class))).thenReturn(CursorPage.of(List.of(task), null, false));
        mockMvc.perform(get("/api/projects/{projectId}/tasks/{id}/subtasks", PROJECT_ID, TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(TASK_ID.toString()));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createTaskService.handle(any())).thenReturn(task);
        mockMvc.perform(post("/api/projects/{projectId}/tasks", PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Task\",\"description\":\"Desc\",\"priority\":\"HIGH\",\"taskType\":\"TASK\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Task updated = Task.builder().id(TASK_ID).projectId(PROJECT_ID).title("Updated").description("Desc").status("TODO").priority("LOW").taskType("TASK").build();
        when(updateTaskService.handle(any())).thenReturn(updated);
        mockMvc.perform(put("/api/projects/{projectId}/tasks/{id}", PROJECT_ID, TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"description\":\"Desc\",\"priority\":\"LOW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateStatusReturnsOk() throws Exception {
        Task updated = Task.builder().id(TASK_ID).projectId(PROJECT_ID).title("Task").description("Desc").status("IN_PROGRESS").priority("HIGH").taskType("TASK").build();
        when(changeTaskStatusService.handle(any())).thenReturn(updated);
        mockMvc.perform(patch("/api/projects/{projectId}/tasks/{id}/status?status=IN_PROGRESS", PROJECT_ID, TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void assignReturnsOk() throws Exception {
        mockMvc.perform(post("/api/projects/{projectId}/tasks/{id}/assign?userId={userId}", PROJECT_ID, TASK_ID, USER_ID))
                .andExpect(status().isOk());
        verify(assignTaskService).handle(any(AssignTaskUserCommand.class));
    }

    @Test
    void unassignReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{id}/assign/{userId}", PROJECT_ID, TASK_ID, USER_ID))
                .andExpect(status().isNoContent());
        verify(assignTaskService).handle(any(UnassignTaskUserCommand.class));
    }

    @Test
    void addTagReturnsOk() throws Exception {
        mockMvc.perform(post("/api/projects/{projectId}/tasks/{id}/tags?tagName=urgent", PROJECT_ID, TASK_ID))
                .andExpect(status().isOk());
        verify(manageTaskTagService).handle(any(AddTaskTagCommand.class));
    }

    @Test
    void removeTagReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{id}/tags/{tagName}", PROJECT_ID, TASK_ID, "urgent"))
                .andExpect(status().isNoContent());
        verify(manageTaskTagService).handle(any(RemoveTaskTagCommand.class));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{id}", PROJECT_ID, TASK_ID))
                .andExpect(status().isNoContent());
        verify(deleteTaskService).handle(any());
    }
}
