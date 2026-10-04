package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.commands.ChangeProjectStatusCommand;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeProjectStatusServiceImplTest {

    @Mock ProjectRepository projectRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ChangeProjectStatusServiceImpl service;

    @Test
    void changesStatus() {
        ChangeProjectStatusCommand cmd = new ChangeProjectStatusCommand(10L, "COMPLETED");
        Project p = Project.builder().id(10L).name("Proj").status("ACTIVE").build();
        when(projectRepository.findById(10L)).thenReturn(Optional.of(p));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project result = service.handle(cmd);

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(projectRepository).save(p);
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenProjectNotFound() {
        ChangeProjectStatusCommand cmd = new ChangeProjectStatusCommand(999L, "COMPLETED");
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
