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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProjectQueryServiceImplTest {

    @Mock ProjectRepository projectRepository;
    @InjectMocks GetProjectQueryServiceImpl service;

    @Test
    void getByIdReturnsProject() {
        Project p = Project.builder().id(10L).name("Proj").build();
        when(projectRepository.findById(10L)).thenReturn(Optional.of(p));
        assertThat(service.handle(new GetProjectByIdQuery(10L))).isEqualTo(p);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetProjectByIdQuery(999L))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByWorkspaceReturnsProjects() {
        Project p = Project.builder().id(10L).build();
        when(projectRepository.findByWorkspaceIdOrderByIdAsc(1L)).thenReturn(List.of(p));
        assertThat(service.handle(new ListProjectsByWorkspaceQuery(1L))).containsExactly(p);
    }
}
