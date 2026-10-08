package com.deepprotech.deepproject.workspaces.services;

import com.deepprotech.deepproject.common.dto.CursorPage;
import com.deepprotech.deepproject.common.exception.ResourceNotFoundException;
import com.deepprotech.deepproject.common.pagination.CursorCodec;
import com.deepprotech.deepproject.common.pagination.CursorKey;
import com.deepprotech.deepproject.common.pagination.CursorPages;
import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.core.WorkspaceMember;
import com.deepprotech.deepproject.workspaces.api.GetWorkspaceQueryService;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceByIdQuery;
import com.deepprotech.deepproject.workspaces.queries.GetWorkspaceBySlugQuery;
import com.deepprotech.deepproject.workspaces.queries.ListUserWorkspacesQuery;
import com.deepprotech.deepproject.workspaces.queries.ListWorkspaceMembersQuery;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceMemberRepository;
import com.deepprotech.deepproject.workspaces.repository.WorkspaceRepository;
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
public class GetWorkspaceQueryServiceImpl implements GetWorkspaceQueryService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public Workspace handle(GetWorkspaceByIdQuery query) {
        return workspaceRepository.findById(query.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", query.workspaceId()));
    }

    @Override
    public Workspace handle(GetWorkspaceBySlugQuery query) {
        return workspaceRepository.findBySlug(query.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found: " + query.slug()));
    }

    @Override
    public CursorPage<Workspace> handle(ListUserWorkspacesQuery query) {
        PageRequest pageable = PageRequest.of(0, query.limit() + 1);
        CursorKey key = CursorCodec.decodeOrNull(query.cursor());
        List<Workspace> workspaces = key == null
                ? workspaceRepository.findWorkspacesByUserId(query.userId(), pageable)
                : workspaceRepository.findWorkspacesByUserIdAfter(query.userId(), key.createdAt(), key.id(), pageable);
        return CursorPages.build(workspaces, query.limit(), Workspace::getCreatedAt, Workspace::getId);
    }

    @Override
    public List<WorkspaceMember> handle(ListWorkspaceMembersQuery query) {
        return workspaceMemberRepository.findByWorkspaceId(query.workspaceId());
    }
}
