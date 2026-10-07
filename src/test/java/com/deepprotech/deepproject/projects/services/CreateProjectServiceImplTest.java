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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProjectServiceImplTest {

    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");
    private static final UUID WS_ID = UUID.fromString("a0000006-0000-0000-0000-000000000001");

    @Mock ProjectRepository projectRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateProjectServiceImpl service;

    @Test
    void createsProject() {
        CreateProjectCommand cmd = new CreateProjectCommand(WS_ID, "Proj", "Desc");
        Project p = Project.builder().id(PROJECT_ID).workspaceId(WS_ID).name("Proj").description("Desc").status("ACTIVE").build();
        when(projectRepository.save(any(Project.class))).thenReturn(p);

        Project result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(PROJECT_ID);
        assertThat(result.getName()).isEqualTo("Proj");
        verify(projectRepository).save(any(Project.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
