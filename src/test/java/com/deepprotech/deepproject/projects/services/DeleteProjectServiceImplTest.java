package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.projects.commands.DeleteProjectCommand;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteProjectServiceImplTest {

    @Mock ProjectRepository projectRepository;
    @InjectMocks DeleteProjectServiceImpl service;

    @Test
    void deletesProject() {
        service.handle(new DeleteProjectCommand(10L));
        verify(projectRepository).deleteById(10L);
    }
}
