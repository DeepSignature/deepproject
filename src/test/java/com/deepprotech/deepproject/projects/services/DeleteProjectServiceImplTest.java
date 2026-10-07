package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.projects.commands.DeleteProjectCommand;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteProjectServiceImplTest {

    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    @Mock ProjectRepository projectRepository;
    @InjectMocks DeleteProjectServiceImpl service;

    @Test
    void deletesProject() {
        service.handle(new DeleteProjectCommand(PROJECT_ID));
        verify(projectRepository).deleteById(PROJECT_ID);
    }
}
