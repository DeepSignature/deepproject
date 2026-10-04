package com.deepprotech.deepproject.comments.web;

import com.deepprotech.deepproject.comments.api.CreateCommentService;
import com.deepprotech.deepproject.comments.api.DeleteCommentService;
import com.deepprotech.deepproject.comments.api.GetCommentQueryService;
import com.deepprotech.deepproject.comments.api.UpdateCommentService;
import com.deepprotech.deepproject.comments.commands.CreateCommentCommand;
import com.deepprotech.deepproject.comments.commands.DeleteCommentCommand;
import com.deepprotech.deepproject.comments.commands.UpdateCommentCommand;
import com.deepprotech.deepproject.comments.dto.CommentResponse;
import com.deepprotech.deepproject.comments.dto.CreateCommentRequest;
import com.deepprotech.deepproject.comments.dto.UpdateCommentRequest;
import com.deepprotech.deepproject.comments.queries.GetCommentByIdQuery;
import com.deepprotech.deepproject.comments.queries.ListCommentsByTaskQuery;
import com.deepprotech.deepproject.core.Comment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CreateCommentService createCommentService;
    private final UpdateCommentService updateCommentService;
    private final DeleteCommentService deleteCommentService;
    private final GetCommentQueryService getCommentQueryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_COMMENT_READ')")
    public ResponseEntity<List<CommentResponse>> list(@PathVariable Long taskId) {
        List<CommentResponse> list = getCommentQueryService.handle(new ListCommentsByTaskQuery(taskId))
                .stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_COMMENT_READ')")
    public ResponseEntity<CommentResponse> get(@PathVariable Long id) {
        Comment c = getCommentQueryService.handle(new GetCommentByIdQuery(id));
        return ResponseEntity.ok(CommentResponse.from(c));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_COMMENT_CREATE')")
    public ResponseEntity<CommentResponse> create(@PathVariable Long taskId,
                                                  @RequestParam Long authorId,
                                                  @Valid @RequestBody CreateCommentRequest request) {
        Comment c = createCommentService.handle(new CreateCommentCommand(taskId, authorId, request.content()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(c));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_COMMENT_UPDATE_OWN')")
    public ResponseEntity<CommentResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateCommentRequest request) {
        Comment c = updateCommentService.handle(new UpdateCommentCommand(id, request.content()));
        return ResponseEntity.ok(CommentResponse.from(c));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_COMMENT_DELETE_OWN') or hasAuthority('PERMISSION_COMMENT_DELETE_ANY')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteCommentService.handle(new DeleteCommentCommand(id));
        return ResponseEntity.noContent().build();
    }
}