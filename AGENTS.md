# AGENTS.md — Deep Project

Spring Boot 4.1.1 monolith structured with Spring Modulith and CQRS pattern.
Authentication via Keycloak OAuth2 Resource Server with in-app role-to-permission mapping (Option B).

## Dev commands

```bash
# Local infrastructure (PowerShell):
.\tools\scripts\start-dev-service.ps1 up                    # Spin up PostgreSQL + Keycloak, run Flyway, seed /test-data
.\tools\scripts\start-dev-service.ps1 up -WithTelemetry     # Also start Grafana LGTM OTel stack
.\tools\scripts\start-dev-service.ps1 up -SkipTestData      # Skip test-data seeding
.\tools\scripts\start-dev-service.ps1 down                  # Stop all containers
.\tools\scripts\start-dev-service.ps1 reset                 # Clean volumes, full fresh setup
.\tools\scripts\start-dev-service.ps1 status                # Show all container status

# Individual service scripts:
.\tools\scripts\start-dev-db.ps1 up          # PostgreSQL + Flyway + seed only
.\tools\scripts\start-dev-db.ps1 migrate     # Flyway migrate on demand
.\tools\scripts\start-dev-db.ps1 info        # Flyway migration status
.\tools\scripts\start-dev-db.ps1 repair      # Flyway repair
.\tools\scripts\start-dev-db.ps1 seed        # Re-run /test-data seed scripts only
.\tools\scripts\start-dev-db.ps1 down        # Stop PostgreSQL + Flyway
.\tools\scripts\start-dev-db.ps1 reset       # Clean DB volumes, fresh migrate + seed
.\tools\scripts\start-keycloak-service.ps1 up    # Start Keycloak
.\tools\scripts\start-keycloak-service.ps1 down  # Stop Keycloak
.\tools\scripts\start-otel-service.ps1 up        # Start Grafana LGTM (OTel)
.\tools\scripts\start-otel-service.ps1 down      # Stop Grafana LGTM

# Application build & run:
./gradlew build             # full build (compile + test)
./gradlew test              # run tests
./gradlew compileJava       # compile only
./gradlew bootRun           # start dev server
```

## Prerequisites

- **Java 21** (enforced via Gradle toolchain)
- **Docker** for local infrastructure (PostgreSQL, Keycloak, Flyway, optional Grafana LGTM)
- **Local Database**: PostgreSQL `localhost:5432` (`postgres` / `postgres` / `deepproject_db`) managed via `tools/scripts/start-dev-db.ps1`.
- **Local Keycloak**: `localhost:8090`, `admin` / `admin`, realm `deepproject`, client `deepproject-api` (public). Managed via `tools/scripts/start-keycloak-service.ps1`.
- **Local OTel (optional)**: Grafana LGTM on `:3000` (UI), `:4317` (gRPC), `:4318` (HTTP). Managed via `tools/scripts/start-otel-service.ps1`.

## Keycloak & Authentication

- **Authentication**: OAuth2 Resource Server JWT (Keycloak) via `spring-boot-starter-oauth2-resource-server`.
- **Authorization**: In-App Role-to-Permission matrix (Option B). Keycloak issues coarse roles (`SYSTEM_ADMIN`, `ORGANIZATION_ADMIN`, `CONTRIBUTOR`, etc.). The application expands them to fine-grained `GrantedAuthority` permissions via `AppRole` enum.
- **Roles**: `SYSTEM_ADMIN`, `ORGANIZATION_ADMIN`, `ORGANIZATION_MEMBER`, `WORKSPACE_ADMIN`, `PROJECT_MANAGER`, `CONTRIBUTOR`, `VIEWER`.
- **Permissions**: 33 granular permissions (`ORG_CREATE`, `WORKSPACE_READ`, `TASK_ASSIGN`, etc.) — see `Permission.java`.
- **Converter**: `KeycloakJwtAuthenticationConverter` extracts `realm_access.roles` → maps roles via `AppRole` → produces `ROLE_*` and `PERMISSION_*` authorities.
- **Principal**: `AuthenticatedUserPrincipal` (identityId, username, email, roles, permissions).
- **Realm config**: `tools/keycloak/realm-export.json` imported on startup via `--import-realm`.
- **Test users**: `admin`/`admin` (SYSTEM_ADMIN), `john_doe`/`john123` (CONTRIBUTOR), `jane_smith`/`jane123` (VIEWER).

## Swagger / OpenAPI

- **Library**: `springdoc-openapi-starter-webmvc-ui:2.8.6` — auto-generates OpenAPI 3.0 spec from controllers and DTOs.
- **Swagger UI**: `http://localhost:8081/swagger-ui.html` — publicly accessible (no auth required to view the UI).
- **API Docs JSON**: `http://localhost:8081/api-docs`.
- **OAuth2 Authorization**: Swagger UI is configured with Keycloak authorization code flow + PKCE. Click **Authorize** → enter client `deepproject-api` → authenticate with Keycloak credentials. The JWT token is automatically sent as `Authorization: Bearer <token>` on subsequent API calls via Swagger UI.
- **Security Scheme**: Defined in `OpenApiConfig.java` (`keycloak_oauth` OAuth2 scheme with auth/token URLs pointing at Keycloak).
- **Unsecured Paths**: `/swagger-ui/**` and `/api-docs/**` are `permitAll()` in `SecurityConfig.java`. All `/api/**` endpoints still require authentication.

## Database Migrations & Local Test Data

- **Schema Migrations**: Stored in `database/scripts/migrations/` (e.g. `V1_20261004_1649__init_schema.sql`, `V1_20261004_1721__add_keycloak_org_identity.sql`). Executed via dedicated Flyway container (`flyway/flyway:11-alpine`) mounted to `database/scripts/migrations/`.
- **Local Test Data**: Stored in root `/test-data/` (e.g. `R_seed_test_data.sql`). These scripts are **never** bundled into the production artifact and run exclusively in local environments via `tools/scripts/start-dev-db.ps1`.

## Architecture & Code Guidelines

### 1. Database Entities Kernel (`com.deepprotech.deepproject.core`)
- All persistent database entities (`User`, `Role`, `Organization`, `OrganizationMember`, `Workspace`, `WorkspaceMember`, `Project`, `ProjectMember`, `Task`, `TaskAssignee`, `TaskTag`, `Comment`, `Notification`) and `BaseEntity` **MUST** live in `com.deepprotech.deepproject.core`.
- The `core` module is configured with `@ApplicationModule(type = OPEN)` allowing all domain modules to reference core entity models directly.
- The `common` module is also configured with `@ApplicationModule(type = OPEN)` for security types.
- Entities must use Lombok `@Data @NoArgsConstructor @AllArgsConstructor @Builder`.

### 2. Standardized Domain Module Layout
Every domain module (`iam`, `organizations`, `workspaces`, `projects`, `tasks`, `comments`, `notifications`) adheres to a uniform structure with specific subpackages:
- **`api/`**: Granular, single-purpose service interfaces (e.g., `CreateWorkspaceService`, `UpdateWorkspaceService`, `GetWorkspaceQueryService`). Marked with `@NamedInterface("api")`.
- **`services/`**: Implementations matching the granular interfaces in `api/` (e.g., `CreateWorkspaceServiceImpl`, `UpdateWorkspaceServiceImpl`). Annotated with `@Slf4j @Service @RequiredArgsConstructor`.
- **`commands/`**: Immutable Java records representing state-changing operations (e.g., `CreateWorkspaceCommand`, `AddOrganizationMemberCommand`).
- **`queries/`**: Immutable Java records representing read operations (e.g., `GetWorkspaceByIdQuery`, `GetMeQuery`).
- **`dto/`**: Web REST request/response records (e.g., `CreateWorkspaceRequest`, `UserProfileResponse`).
- **`constants/`**: Domain-specific enums and constant classes (e.g., `WorkspaceRole`, `OrganizationRole`).
- **`events/`**: Inter-module domain event records (e.g., `WorkspaceCreatedEvent`, `OrganizationCreatedEvent`). Marked with `@NamedInterface("events")`.
- **`web/`**: Spring MVC REST controllers with `@PreAuthorize("hasAuthority('PERMISSION_...')")`.

### 3. CQRS Pattern & Granular Services
- Do **NOT** build fat monolithic CRUD services (e.g. `WorkspaceService`).
- Each business use case must have a specific, single-purpose service interface in `api/` and implementation in `services/`.
- **Write Operations (Commands)**: Use `@Transactional`, modify state via Spring JDBC, publish domain events via `ApplicationEventPublisher`.
- **Read Operations (Queries)**: Use `@Transactional(readOnly = true)`, return projection records or core entities.

### 4. Database Auditing (`_h` Tables)
- Audit history is automated via PostgreSQL shadow tables (`<table>_h`) and `fn_audit_history()` trigger function.
- Flyway migration `V1_20261004_1649__init_schema.sql` automatically registers all base tables in an automated loop.
- User context is injected dynamically by `AuditContextInterceptor` executing `SET LOCAL app.current_user = '...'`.

### 5. Testing & Modulith Boundaries
- Module isolation and boundary enforcement is tested via Spring Modulith in `ModuleArchitectureTests.java`.
- Run `./gradlew test --tests "com.deepprotech.deepproject.ModuleArchitectureTests"` to verify architecture boundaries.