# Deep Project

Spring Boot 4.1.1 monolithic application structured with Spring Modulith and CQRS. Authentication via Keycloak OAuth2 Resource Server with in-app role-to-permission mapping.

## Quick Start

```powershell
# 1. Start all local infrastructure (PostgreSQL + Keycloak + Flyway + seed data)
.\tools\scripts\start-dev-service.ps1 up

# 2. Start the application
./gradlew bootRun

# 3. Open Swagger UI
# http://localhost:8081/swagger-ui.html
```

## Prerequisites

- **Java 21** (enforced via Gradle toolchain)
- **Docker** for local infrastructure

## Local Services

| Service | URL | Credentials |
|---------|-----|-------------|
| App Server | `http://localhost:8081` | — |
| Swagger UI | `http://localhost:8081/swagger-ui.html` | — |
| Keycloak Admin | `http://localhost:8090` | `admin` / `admin` |
| PostgreSQL | `localhost:5432` | `postgres` / `postgres` |
| Grafana (optional) | `http://localhost:3000` | — |

## Dev Commands

```powershell
# Infrastructure
.\tools\scripts\start-dev-service.ps1 up                  # PostgreSQL + Keycloak + Flyway + seed
.\tools\scripts\start-dev-service.ps1 up -WithTelemetry   # + Grafana LGTM OTel
.\tools\scripts\start-dev-service.ps1 down                # Stop everything
.\tools\scripts\start-dev-service.ps1 reset               # Full teardown + fresh setup

# Individual services
.\tools\scripts\start-dev-db.ps1 up        # DB + Flyway + seed
.\tools\scripts\start-dev-db.ps1 reset     # Clean DB + remigrate + reseed
.\tools\scripts\start-keycloak-service.ps1 up

# Build & test
./gradlew build        # Compile + test
./gradlew test         # Run tests
./gradlew bootRun      # Start dev server
```

## Test Users

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin` | SYSTEM_ADMIN |
| `john_doe` | `john123` | CONTRIBUTOR |
| `jane_smith` | `jane123` | VIEWER |

## Architecture

### Domain Modules

| Module | Package | Responsibility |
|--------|---------|----------------|
| IAM | `iam` | Users, roles, authentication |
| Organizations | `organizations` | Multi-tenant orgs and members |
| Workspaces | `workspaces` | Project workspaces and members |
| Projects | `projects` | Projects and members |
| Tasks | `tasks` | Tasks, assignees, tags |
| Comments | `comments` | Task comments |
| Notifications | `notifications` | User notifications |

### Module Layout (standardized across all domains)

```
<module>/
├── api/          # Service interfaces (@NamedInterface)
├── services/     # Service implementations
├── commands/     # Write-operation records
├── queries/      # Read-operation records
├── dto/          # REST request/response records
├── constants/    # Domain enums
├── events/       # Inter-module event records
├── web/          # REST controllers
└── repository/   # Spring Data JPA repositories
```

### CQRS Pattern

- **Commands**: Immutable records → `@Transactional` service → mutate state → publish domain event
- **Queries**: Immutable records → `@Transactional(readOnly = true)` service → return projections
- Services are single-purpose (e.g. `CreateWorkspaceService`, `GetWorkspaceQueryService`), never monolithic CRUD

### Authentication & Authorization

- Keycloak issues coarse roles (e.g. `SYSTEM_ADMIN`, `CONTRIBUTOR`)
- App expands roles to 33 fine-grained permissions via `AppRole` enum
- Controllers use `@PreAuthorize("hasAuthority('PERMISSION_...')")`
- Swagger UI has built-in OAuth2 PKCE flow — click **Authorize** to get a token

### Database

- PostgreSQL with Flyway migrations in `database/scripts/migrations/`
- Automated audit (`*_h` shadow tables) via trigger functions
- Local test data in `/test-data/` (not shipped to production)

### Key Files

| File | Purpose |
|------|---------|
| `AGENTS.md` | Full architecture reference |
| `OpenApiConfig.java` | Swagger/OpenAPI OAuth2 setup |
| `SecurityConfig.java` | Security filter chain |
| `Permission.java` | All permission constants |
| `AppRole.java` | Role-to-permission mapping |
| `ModuleArchitectureTests.java` | Modulith boundary verification |

## Swagger

After starting the application, open `http://localhost:8081/swagger-ui.html`. Click **Authorize**, enter client `deepproject-api`, and sign in with a Keycloak test user. The token is automatically attached to all API calls.