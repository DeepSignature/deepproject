package com.deepprotech.deepproject.projects.services;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Project;
import com.deepprotech.deepproject.projects.api.GetProjectQueryService;
import com.deepprotech.deepproject.projects.queries.GetProjectByIdQuery;
import com.deepprotech.deepproject.projects.queries.ListProjectsByWorkspaceQuery;
import com.deepprotech.deepproject.projects.repository.ProjectRepository;
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
public class GetProjectQueryServiceImpl implements GetProjectQueryService {

    private final ProjectRepository projectRepository;

    @Override
    public Project handle(GetProjectByIdQuery query) {
        return projectRepository.findById(query.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", query.projectId()));
    }

    @Override
    public CursorPage<Project> handle(ListProjectsByWorkspaceQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        List<Project> projects = key == null
                ? projectRepository.findByWorkspaceId(query.workspaceId(), pageable)
                : projectRepository.findByWorkspaceIdAfter(query.workspaceId(), key.createdAt(), key.id(), pageable);
        return CursorPages.build(projects, query.limit(), Project::getCreatedAt, Project::getId);
    }
}
