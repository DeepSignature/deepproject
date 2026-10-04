package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.CreateProjectCommand;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProjectServiceImplTest {

    @Mock ProjectRepository projectRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateProjectServiceImpl service;

    @Test
    void createsProject() {
        CreateProjectCommand cmd = new CreateProjectCommand(1L, "Proj", "Desc");
        Project p = Project.builder().id(10L).workspaceId(1L).name("Proj").description("Desc").status("ACTIVE").build();
        when(projectRepository.save(any(Project.class))).thenReturn(p);

        Project result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Proj");
        verify(projectRepository).save(any(Project.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
