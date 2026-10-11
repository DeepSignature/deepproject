package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.queries.GetSprintByIdQuery;
import com.deepprotech.deepproject.sprints.queries.ListSprintsByProjectQuery;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSprintQueryServiceImplTest {

    private static final UUID SPRINT_ID = UUID.fromString("a0000002-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_ID = UUID.fromString("a0000002-0000-0000-0000-0000000999");
    private static final UUID PROJECT_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    @Mock SprintRepository sprintRepository;
    @InjectMocks GetSprintQueryServiceImpl service;

    @Test
    void getByIdReturnsSprint() {
        Sprint s = Sprint.builder().id(SPRINT_ID).name("Sprint").build();
        when(sprintRepository.findById(SPRINT_ID)).thenReturn(Optional.of(s));
        assertThat(service.handle(new GetSprintByIdQuery(SPRINT_ID))).isEqualTo(s);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(sprintRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.handle(new GetSprintByIdQuery(UNKNOWN_ID))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByProjectReturnsSprints() {
        Sprint s = Sprint.builder().id(SPRINT_ID).build();
        when(sprintRepository.findByProjectId(eq(PROJECT_ID), any(Pageable.class))).thenReturn(List.of(s));
        assertThat(service.handle(new ListSprintsByProjectQuery(PROJECT_ID, 20, null)).items()).containsExactly(s);
    }
}
