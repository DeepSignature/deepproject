package com.deepprotech.deepproject.comments.web;

import com.deepprotech.deepproject.comments.api.CreateCommentService;
import com.deepprotech.deepproject.comments.api.DeleteCommentService;
import com.deepprotech.deepproject.comments.api.GetCommentQueryService;
import com.deepprotech.deepproject.comments.api.UpdateCommentService;
import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.Comment;
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
class CommentControllerTest {

    @Mock CreateCommentService createCommentService;
    @Mock UpdateCommentService updateCommentService;
    @Mock DeleteCommentService deleteCommentService;
    @Mock GetCommentQueryService getCommentQueryService;

    private MockMvc mockMvc;

    private static final UUID COMMENT_ID = UUID.fromString("a0000012-0000-0000-0000-000000000001");
    private static final UUID TASK_ID = UUID.fromString("a0000009-0000-0000-0000-000000000001");
    private static final UUID USER_ID_2 = UUID.fromString("a0000002-0000-0000-0000-000000000002");

    private final Comment comment = Comment.builder().id(COMMENT_ID).taskId(TASK_ID).authorId(USER_ID_2).content("Nice!").build();

    @BeforeEach
    void setUp() {
        CommentController controller = new CommentController(createCommentService, updateCommentService,
                deleteCommentService, getCommentQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listReturnsComments() throws Exception {
        when(getCommentQueryService.handle(any(ListCommentsByTaskQuery.class))).thenReturn(CursorPage.of(List.of(comment), null, false));
        mockMvc.perform(get("/api/tasks/{taskId}/comments", TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(COMMENT_ID.toString()));
    }

    @Test
    void getReturnsComment() throws Exception {
        when(getCommentQueryService.handle(any(GetCommentByIdQuery.class))).thenReturn(comment);
        mockMvc.perform(get("/api/tasks/{taskId}/comments/{id}", TASK_ID, COMMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Nice!"));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createCommentService.handle(any())).thenReturn(comment);
        mockMvc.perform(post("/api/tasks/{taskId}/comments?authorId=" + USER_ID_2, TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Nice!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Nice!"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        Comment updated = Comment.builder().id(COMMENT_ID).taskId(TASK_ID).authorId(USER_ID_2).content("Updated").build();
        when(updateCommentService.handle(any())).thenReturn(updated);
        mockMvc.perform(put("/api/tasks/{taskId}/comments/{id}", TASK_ID, COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/{taskId}/comments/{id}", TASK_ID, COMMENT_ID))
                .andExpect(status().isNoContent());
        verify(deleteCommentService).handle(any());
    }
}
