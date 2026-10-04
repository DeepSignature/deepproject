---
name: springboot-patterns
description: Spring Boot architecture patterns, REST API design, layered services, data access, caching, async processing, and logging. Use for Java Spring Boot backend work.
origin: ECC
---

# Spring Boot Development Patterns & Guidelines

## 1. Domain Module Layout (CQRS + Spring Modulith)

Every domain module adheres to a strict subpackage structure:

```
com.deepprotech.deepproject.<module>/
├── api/             # Single-purpose granular service interfaces (@NamedInterface("api"))
├── services/        # Service implementations matching api/ interfaces (@Slf4j @Service @RequiredArgsConstructor)
├── commands/        # State-changing immutable records (CreateXCommand, UpdateXCommand)
├── queries/         # Read-only query records (GetXByIdQuery, ListXQuery)
├── dto/             # Web REST request/response records (CreateXRequest, XResponse)
├── constants/       # Domain enums & constants (Status, Priority, Role)
├── events/          # Domain event records for inter-module decoupling (@NamedInterface("events"))
└── web/             # Spring MVC REST Controllers
```

## 2. Core Entities Kernel (`core/`)

- All persistent database entities (`User`, `Role`, `Workspace`, `Project`, `Task`, `Comment`, `Notification`, etc.) live in `com.deepprotech.deepproject.core`.
- Annotated with `@ApplicationModule(type = OPEN)` to allow all domain modules to access core models.
- Entities use Lombok `@Data @NoArgsConstructor @AllArgsConstructor @Builder`.

## 3. CQRS & Granular Single-Purpose Services

Avoid fat monolithic service classes. Split use-cases into granular interfaces and implementations:

```java
// api/CreateWorkspaceService.java
package com.deepprotech.deepproject.workspaces.api;

import com.deepprotech.deepproject.core.Workspace;
import com.deepprotech.deepproject.workspaces.commands.CreateWorkspaceCommand;

public interface CreateWorkspaceService {
    Workspace handle(CreateWorkspaceCommand command);
}

// services/CreateWorkspaceServiceImpl.java
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWorkspaceServiceImpl implements CreateWorkspaceService {
    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Workspace handle(CreateWorkspaceCommand command) {
        // execute write operation
        // publish domain event
    }
}
```

## 4. Query Services (Read Operations)

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWorkspaceQueryServiceImpl implements GetWorkspaceQueryService {
    private final JdbcTemplate jdbc;
    // query implementations
}
```

## 5. REST Controllers

Controllers coordinate single-purpose command and query services:

```java
@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    private final CreateWorkspaceService createWorkspaceService;
    private final GetWorkspaceQueryService getWorkspaceQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> get(@PathVariable Long id) {
        Workspace ws = getWorkspaceQueryService.handle(new GetWorkspaceByIdQuery(id));
        return ResponseEntity.ok(WorkspaceResponse.from(ws));
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> create(@Valid @RequestBody CreateWorkspaceRequest req,
                                                    @RequestParam Long ownerId) {
        Workspace ws = createWorkspaceService.handle(new CreateWorkspaceCommand(req.name(), req.slug(), req.description(), ownerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkspaceResponse.from(ws));
    }
}
```

## 6. Audit Logging (`_h` Shadow Tables)

- Flyway migrations automatically provision `_h` shadow tables and triggers using `fn_enable_audit(target_table)`.
- User auditing context is propagated via `AuditContextInterceptor` executing `SET LOCAL app.current_user = '...'`.
