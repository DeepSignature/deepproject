# AGENTS.md — Deep Project

Spring Boot 4.1.1 monolith structured with Spring Modulith.

## Dev commands

```bash
./gradlew build             # full build (compile + test)
./gradlew test              # run tests
./gradlew compileJava       # compile only (includes DGS codegen)
./gradlew generateJava      # DGS GraphQL client codegen only
./gradlew bootRun           # start dev server (Docker Compose auto-started)
./gradlew nativeCompile     # GraalVM native image
./gradlew nativeTest        # run tests in native image
```

## Prerequisites

- **Java 21** (enforced via Gradle toolchain)
- **Docker** — `compose.yaml` provides PostgreSQL (port 5432) + Grafana LGTM (OTel on 4317/4318, UI on 3000)
- Dev services are auto-wired by `spring-boot-docker-compose` on the classpath — no manual datasource config needed for local dev.

## Architecture

- **Spring Modulith** (v2.1.1) enforces module boundaries. Create domain packages directly under `com.deepprotech.deepproject` (e.g. `.orders`, `.inventory`). Modulith tests verify module isolation.
- **Flyway** migrations live in `src/main/resources/db/migration/` (currently empty). Use standard `V{n}__description.sql` naming.
- **Netflix DGS Codegen** — place remote GraphQL schemas (`.graphqls`) in `src/main/resources/graphql-client/`. Generated Java client types land in `com.deepprotech.deepproject.codegen` on compile.
- **Hazelcast** is on the classpath for distributed caching.
- **JDBC session** storage (Spring Session JDBC).
- **OpenTelemetry** wired via Grafana LGTM container.

## Testing

- JUnit 5 platform. Modulith test starter included — use `@ApplicationModuleTest` for module-scoped tests.
- The context-load test (`DeepprojectApplicationTests`) requires Docker (PostgreSQL) to be running, since Spring Boot autoconfigures `DataSource` from the compose config.
- **No test profile** is pre-configured — `application.properties` is minimal.