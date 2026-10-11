package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.commands.CreateSprintCommand;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
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
class CreateSprintServiceImplTest {

    private static final UUID SPRINT_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    @Mock SprintRepository sprintRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks CreateSprintServiceImpl service;

    @Test
    void createsSprint() {
        CreateSprintCommand cmd = new CreateSprintCommand(PROJECT_ID, "Sprint 1", "Desc", null, null);
        Sprint s = Sprint.builder().id(SPRINT_ID).projectId(PROJECT_ID).name("Sprint 1").description("Desc").status("PLANNED").build();
        when(sprintRepository.save(any(Sprint.class))).thenReturn(s);

        Sprint result = service.handle(cmd);

        assertThat(result.getId()).isEqualTo(SPRINT_ID);
        assertThat(result.getName()).isEqualTo("Sprint 1");
        assertThat(result.getStatus()).isEqualTo("PLANNED");
        verify(sprintRepository).save(any(Sprint.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
