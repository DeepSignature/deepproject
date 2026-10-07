package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProjectQueryServiceImplTest {

    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("a0000001-0000-0000-0000-0000000999");
    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");

    @Mock ProjectRepository projectRepository;
    @InjectMocks GetProjectQueryServiceImpl service;

    @Test
    void getByIdReturnsProject() {
        Project p = Project.builder().id(PROJECT_ID).name("Proj").build();
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(p));
        assertThat(service.handle(new GetProjectByIdQuery(PROJECT_ID))).isEqualTo(p);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(projectRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetProjectByIdQuery(UNKNOWN_ID))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByWorkspaceReturnsProjects() {
        Project p = Project.builder().id(PROJECT_ID).build();
        when(projectRepository.findByWorkspaceIdOrderByIdAsc(WS_ID)).thenReturn(List.of(p));
        assertThat(service.handle(new ListProjectsByWorkspaceQuery(WS_ID))).containsExactly(p);
    }
}
