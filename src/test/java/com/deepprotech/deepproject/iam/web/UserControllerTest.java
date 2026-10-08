package com.deepprotech.deepproject.iam.web;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.core.User;
import com.deepprotech.deepproject.iam.api.CreateUserService;
import com.deepprotech.deepproject.iam.api.DeactivateUserService;
import com.deepprotech.deepproject.iam.api.GetMeQueryService;
import com.deepprotech.deepproject.iam.api.GetUserQueryService;
import com.deepprotech.deepproject.iam.api.UpdateUserService;
import com.deepprotech.deepproject.iam.queries.GetUserByIdQuery;
import com.deepprotech.deepproject.iam.queries.ListUsersQuery;
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
class UserControllerTest {

    private static final UUID USER_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Mock CreateUserService createUserService;
    @Mock UpdateUserService updateUserService;
    @Mock DeactivateUserService deactivateUserService;
    @Mock GetUserQueryService getUserQueryService;
    @Mock GetMeQueryService getMeQueryService;

    private MockMvc mockMvc;

    private final User user = User.builder().id(USER_ID).identityId("id-1").username("testuser").email("test@test.com").displayName("Test").active(true).build();

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(createUserService, updateUserService, deactivateUserService, getUserQueryService, getMeQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getReturnsUser() throws Exception {
        when(getUserQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void listReturnsUsers() throws Exception {
        when(getUserQueryService.handle(any(ListUsersQuery.class))).thenReturn(CursorPage.of(List.of(user), null, false));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(USER_ID.toString()));
    }

    @Test
    void createReturnsCreated() throws Exception {
        when(createUserService.handle(any())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"identityId":"id-1","username":"testuser","email":"test@test.com","displayName":"Test"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID.toString()));
    }

    @Test
    void updateReturnsOk() throws Exception {
        User updated = User.builder().id(USER_ID).identityId("id-1").username("testuser").email("test@test.com").displayName("Updated").active(true).build();
        when(updateUserService.handle(any())).thenReturn(updated);

        mockMvc.perform(put("/api/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Updated"));
    }

    @Test
    void deactivateReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", USER_ID))
                .andExpect(status().isNoContent());
        verify(deactivateUserService).handle(any());
    }
}
