package com.deepprotech.deepproject.sprints.services;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Sprint;
import com.deepprotech.deepproject.sprints.api.GetSprintQueryService;
import com.deepprotech.deepproject.sprints.queries.GetSprintByIdQuery;
import com.deepprotech.deepproject.sprints.queries.ListSprintsByProjectQuery;
import com.deepprotech.deepproject.sprints.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSprintQueryServiceImpl implements GetSprintQueryService {

    private final SprintRepository sprintRepository;

    @Override
    public Sprint handle(GetSprintByIdQuery query) {
        return sprintRepository.findById(query.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", query.sprintId()));
    }

    @Override
    public CursorPage<Sprint> handle(ListSprintsByProjectQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        List<Sprint> sprints = key == null
                ? sprintRepository.findByProjectId(query.projectId(), pageable)
                : sprintRepository.findByProjectIdAfter(query.projectId(), key.createdAt(), key.id(), pageable);
        return CursorPages.build(sprints, query.limit(), Sprint::getCreatedAt, Sprint::getId);
    }
}
