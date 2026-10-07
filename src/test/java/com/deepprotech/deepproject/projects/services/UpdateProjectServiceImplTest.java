package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.UpdateProjectCommand;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProjectServiceImplTest {

    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("a0000001-0000-0000-0000-0000000999");

    @Mock ProjectRepository projectRepository;
    @InjectMocks UpdateProjectServiceImpl service;

    @Test
    void updatesProjectReturnsIt() {
        UpdateProjectCommand cmd = new UpdateProjectCommand(PROJECT_ID, "New Proj", "New Desc");
        Project p = Project.builder().id(PROJECT_ID).name("Old Proj").description("Old Desc").build();
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(p));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project result = service.handle(cmd);

        assertThat(result.getName()).isEqualTo("New Proj");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        verify(projectRepository).save(p);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateProjectCommand cmd = new UpdateProjectCommand(UNKNOWN_ID, "New Proj", "New Desc");
        when(projectRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
