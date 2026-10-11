package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.sprints.commands.DeleteSprintCommand;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteSprintServiceImplTest {

    private static final UUID SPRINT_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");

    @Mock SprintRepository sprintRepository;
    @InjectMocks DeleteSprintServiceImpl service;

    @Test
    void deletesSprint() {
        service.handle(new DeleteSprintCommand(SPRINT_ID));
        verify(sprintRepository).deleteById(SPRINT_ID);
    }
}
