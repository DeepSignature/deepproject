package com.deepprotech.deepproject.tasks.web;

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

    @Mock CreateTaskService createTaskService;
    @Mock UpdateTaskService updateTaskService;
    @Mock ChangeTaskStatusService changeTaskStatusService;
    @Mock AssignTaskService assignTaskService;
    @Mock ManageTaskTagService manageTaskTagService;
    @Mock DeleteTaskService deleteTaskService;
    @Mock GetTaskQueryService getTaskQueryService;

    private MockMvc mockMvc;

    private final Task task = Task.builder().id(1L).projectId(1L).title("Task").description("Desc").status("TODO").priority("HIGH").taskType("TASK").build();

    @BeforeEach
    void setUp() {
        TaskController controller = new TaskController(createTaskService, updateTaskService, changeTaskStatusService,
                assignTaskService, manageTaskTagService, deleteTaskService, getTaskQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listReturnsTasks() throws Exception {
        when(getTaskQueryService.handle(any(ListTasksByProjectQuery.class))).thenReturn(List.of(task));
        mockMvc.perform(get("/api/projects/{projectId}/tasks", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getReturnsTask() throws Exception {
        when(getTaskQueryService.handle(any(GetTaskByIdQuery.class))).thenReturn(task);
        mockMvc.perform(get("/api/projects/{projectId}/tasks/{id}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task"));
    }

    @Test
    void subtasksReturnsList() throws Exception {
        when(getTaskQueryService.handle(any(ListSubtasksQuery.class))).thenReturn(List.of(task));
        mockMvc.perform(get("/api/projects/{projectId}/tasks/{id}/subtasks", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createTaskService.handle(any())).thenReturn(task);
        mockMvc.perform(post("/api/projects/{projectId}/tasks", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Task\",\"description\":\"Desc\",\"priority\":\"HIGH\",\"taskType\":\"TASK\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Task updated = Task.builder().id(1L).projectId(1L).title("Updated").description("Desc").status("TODO").priority("LOW").taskType("TASK").build();
        when(updateTaskService.handle(any())).thenReturn(updated);
        mockMvc.perform(put("/api/projects/{projectId}/tasks/{id}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"description\":\"Desc\",\"priority\":\"LOW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateStatusReturnsOk() throws Exception {
        Task updated = Task.builder().id(1L).projectId(1L).title("Task").description("Desc").status("IN_PROGRESS").priority("HIGH").taskType("TASK").build();
        when(changeTaskStatusService.handle(any())).thenReturn(updated);
        mockMvc.perform(patch("/api/projects/{projectId}/tasks/{id}/status?status=IN_PROGRESS", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void assignReturnsOk() throws Exception {
        mockMvc.perform(post("/api/projects/{projectId}/tasks/{id}/assign?userId=2", 1L, 1L))
                .andExpect(status().isOk());
        verify(assignTaskService).handle(any(AssignTaskUserCommand.class));
    }

    @Test
    void unassignReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{id}/assign/{userId}", 1L, 1L, 2L))
                .andExpect(status().isNoContent());
        verify(assignTaskService).handle(any(UnassignTaskUserCommand.class));
    }

    @Test
    void addTagReturnsOk() throws Exception {
        mockMvc.perform(post("/api/projects/{projectId}/tasks/{id}/tags?tagName=urgent", 1L, 1L))
                .andExpect(status().isOk());
        verify(manageTaskTagService).handle(any(AddTaskTagCommand.class));
    }

    @Test
    void removeTagReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{id}/tags/{tagName}", 1L, 1L, "urgent"))
                .andExpect(status().isNoContent());
        verify(manageTaskTagService).handle(any(RemoveTaskTagCommand.class));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{id}", 1L, 1L))
                .andExpect(status().isNoContent());
        verify(deleteTaskService).handle(any());
    }
}
