package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.commands.ChangeSprintStatusCommand;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeSprintStatusServiceImplTest {

    private static final UUID SPRINT_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("a0000002-0000-0000-0000-0000000999");

    @Mock SprintRepository sprintRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks ChangeSprintStatusServiceImpl service;

    @Test
    void changesStatusAndActivatesSprint() {
        ChangeSprintStatusCommand cmd = new ChangeSprintStatusCommand(SPRINT_ID, "ACTIVE");
        Sprint s = Sprint.builder().id(SPRINT_ID).name("Sprint").status("PLANNED").active(false).build();
        when(sprintRepository.findById(SPRINT_ID)).thenReturn(Optional.of(s));
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(inv -> inv.getArgument(0));

        Sprint result = service.handle(cmd);

        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.isActive()).isTrue();
        verify(sprintRepository).save(s);
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void throwsWhenSprintNotFound() {
        ChangeSprintStatusCommand cmd = new ChangeSprintStatusCommand(UNKNOWN_ID, "ACTIVE");
        when(sprintRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void throwsWhenStatusInvalid() {
        ChangeSprintStatusCommand cmd = new ChangeSprintStatusCommand(SPRINT_ID, "BOGUS");
        Sprint s = Sprint.builder().id(SPRINT_ID).name("Sprint").status("PLANNED").build();
        when(sprintRepository.findById(SPRINT_ID)).thenReturn(Optional.of(s));
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(IllegalArgumentException.class);
    }
}
