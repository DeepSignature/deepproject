package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.commands.UpdateSprintCommand;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
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
class UpdateSprintServiceImplTest {

    private static final UUID SPRINT_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("a0000002-0000-0000-0000-0000000999");

    @Mock SprintRepository sprintRepository;
    @InjectMocks UpdateSprintServiceImpl service;

    @Test
    void updatesSprintReturnsIt() {
        UpdateSprintCommand cmd = new UpdateSprintCommand(SPRINT_ID, "New Sprint", "New Desc", null, null);
        Sprint s = Sprint.builder().id(SPRINT_ID).name("Old Sprint").description("Old Desc").build();
        when(sprintRepository.findById(SPRINT_ID)).thenReturn(Optional.of(s));
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(inv -> inv.getArgument(0));

        Sprint result = service.handle(cmd);

        assertThat(result.getName()).isEqualTo("New Sprint");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        verify(sprintRepository).save(s);
    }

    @Test
    void throwsWhenNotFound() {
        UpdateSprintCommand cmd = new UpdateSprintCommand(UNKNOWN_ID, "New Sprint", "New Desc", null, null);
        when(sprintRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(cmd)).isInstanceOf(ResourceNotFoundException.class);
    }
}
