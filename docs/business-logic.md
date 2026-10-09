# Business Logic

This document describes the domain model, business rules, and invariants that govern Deep Project. It complements the architectural reference in `AGENTS.md` (structure, CQRS, Modulith) by focusing on *what* the system does and the rules it enforces, not *how* it is wired together.

## 1. Domain Overview

Deep Project is a multi-tenant project/task management system. It models a four-level hierarchy:

```
Organization → Workspace → Project → Task
     │              │           │        └── Comment, Tag, Assignee
     │              │           └── ProjectMember
     │              └── WorkspaceMember
     └── OrganizationMember
```

A `User` (provisioned from Keycloak) participates in this hierarchy through membership records at each level. Roles are defined at two distinct layers:

- **Global roles** (from the Keycloak JWT) — expanded in-app to fine-grained permissions.
- **Membership roles** (stored per membership row) — describe the user's standing *within* a specific organization/workspace/project.

## 2. Entity Model

All persistent entities live in `com.deepprotech.deepproject.core`. Most extend `BaseEntity`, which supplies:

- `id` (UUID, server-generated)
- `createdAt` / `updatedAt` (database-managed timestamps)
- `createdBy` / `updatedBy` (audit context, injected by `AuditContextInterceptor`)
- `version` (`@Version`, optimistic locking)

| Entity | Table | Key fields | Notes |
|--------|-------|------------|-------|
| `User` | `users` | `identityId` (unique), `username` (unique), `email` (unique), `active` | Mirrors a Keycloak identity via `identityId` (JWT subject). Default `active = true`. |
| `Role` | `roles` | `name` (unique) | Legacy/global role catalog, managed via `assignRoleToUser` / `removeRoleFromUser`. |
| `Organization` | `organizations` | `identifier` (unique), `name` | Top-level tenant. |
| `OrganizationMember` | `organization_members` | `organizationId`, `userId`, `role` | Default role `ORGANIZATION_MEMBER`. |
| `Workspace` | `workspaces` | `name`, `slug` (unique), `ownerId`, `organizationId` | Optional link to an organization. |
| `WorkspaceMember` | `workspace_members` | `workspaceId`, `userId`, `role` | Default role `MEMBER`. |
| `Project` | `projects` | `workspaceId`, `name`, `status`, `startDate`, `endDate` | Extends `BaseEntity`-like fields inline (not via inheritance). Default `status = ACTIVE`. |
| `ProjectMember` | `project_members` | `projectId`, `userId`, `role` | Default role `MEMBER`. |
| `Task` | `tasks` | `projectId`, `parentTaskId`, `title`, `status`, `priority`, `taskType`, `dueDate`, `estimatedHours`, `actualHours` | Supports subtasks via `parentTaskId`. |
| `TaskAssignee` | `task_assignees` | `taskId`, `userId` | Many-to-many task ↔ user. |
| `TaskTag` | `task_tags` | `taskId`, `tagName` | Free-form string tags. |
| `Comment` | `comments` | `taskId`, `authorId`, `content` | Content limited by `CommentConstants.MAX_COMMENT_LENGTH` (10000). |
| `Notification` | `notifications` | `userId`, `title`, `message`, `notificationType`, `status`, `entityType`, `entityId` | Polymorphic reference via `entityType` + `entityId`. |

## 3. Authorization Model (RBAC)

Authentication is delegated to Keycloak (OAuth2 Resource Server). Authorization is a two-step in-app expansion:

1. `KeycloakJwtAuthenticationConverter` extracts roles from the JWT (`realm_access.roles`, falling back to `resource_access.<client>.roles`).
2. Each role is mapped through the `AppRole` enum into a set of `Permission`s.
3. The principal (`AuthenticatedUserPrincipal`) carries both `ROLE_*` and `PERMISSION_*` authorities. Controllers gate access with `@PreAuthorize("hasAuthority('PERMISSION_...')")`.

### 3.1 Role → Permission Matrix

| Permission | SYSTEM_ADMIN | ORG_ADMIN | ORG_MEMBER | WORKSPACE_ADMIN | PROJECT_MANAGER | CONTRIBUTOR | VIEWER |
|---|---|---|---|---|---|---|---|
| `SYSTEM_ADMIN` | ✅ | | | | | | |
| `ORG_CREATE` | ✅ | | | | | | |
| `ORG_READ` | ✅ | ✅ | ✅ | | | | |
| `ORG_UPDATE` | ✅ | ✅ | | | | | |
| `ORG_DELETE` | ✅ | | | | | | |
| `ORG_MEMBER_MANAGE` | ✅ | ✅ | | | | | |
| `WORKSPACE_CREATE` | ✅ | ✅ | | | | | |
| `WORKSPACE_READ` | ✅ | ✅ | ✅ | ✅ | | | ✅ |
| `WORKSPACE_UPDATE` | ✅ | ✅ | | ✅ | | | |
| `WORKSPACE_DELETE` | ✅ | ✅ | | | | | |
| `WORKSPACE_MEMBER_MANAGE` | ✅ | ✅ | | ✅ | | | |
| `PROJECT_CREATE` | ✅ | ✅ | | ✅ | | | |
| `PROJECT_READ` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `PROJECT_UPDATE` | ✅ | ✅ | | ✅ | ✅ | | |
| `PROJECT_DELETE` | ✅ | ✅ | | ✅ | | | |
| `PROJECT_MEMBER_MANAGE` | ✅ | ✅ | | ✅ | ✅ | | |
| `TASK_CREATE` | ✅ | ✅ | | ✅ | ✅ | ✅ | |
| `TASK_READ` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `TASK_UPDATE` | ✅ | ✅ | | ✅ | ✅ | ✅ | |
| `TASK_DELETE` | ✅ | ✅ | | ✅ | ✅ | | |
| `TASK_ASSIGN` | ✅ | ✅ | | ✅ | ✅ | | |
| `TASK_STATUS_CHANGE` | ✅ | ✅ | | ✅ | ✅ | ✅ | |
| `COMMENT_CREATE` | ✅ | ✅ | | ✅ | ✅ | ✅ | |
| `COMMENT_READ` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `COMMENT_UPDATE_OWN` | ✅ | ✅ | | ✅ | ✅ | ✅ | |
| `COMMENT_DELETE_OWN` | ✅ | ✅ | | ✅ | ✅ | | |
| `COMMENT_DELETE_ANY` | ✅ | ✅ | | ✅ | ✅ | | |
| `NOTIFICATION_READ` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `NOTIFICATION_MANAGE` | ✅ | ✅ | | ✅ | | | |

### 3.2 Membership Roles

Stored as strings on membership rows. These are **distinct** from global `AppRole`s and are used for tenant-internal governance (they are expanded to permissions in `GetMeQueryServiceImpl` by mapping through `AppRole.fromName(...)`):

- `OrganizationRole`: `ORGANIZATION_ADMIN`, `ORGANIZATION_MEMBER`
- `WorkspaceRole`: `OWNER`, `ADMIN`, `MEMBER`, `VIEWER`
- `ProjectMember.role`: default `MEMBER`

> Note: `UserRole` (`ADMIN`, `MEMBER`, `VIEWER`) exists in `iam/constants` but the active role catalog is `AppRole`; the two are not wired together.

## 4. Business Rules by Module

### 4.1 IAM (Users & Identity)

- **Provisioning on first access**: `GetMeQueryServiceImpl` looks up the user by `identityId`; if absent it auto-provisions a `User` with a synthetic username (`kc_<identityId prefix>`), placeholder email, `active = true`. This is the self-registration path for any authenticated Keycloak identity.
- **Global role/permission resolution**: the token's roles are expanded via `AppRole` into the effective global permission set returned in the profile.
- **User lifecycle**: users are deactivated (soft — `active = false`), never hard-deleted. `DeactivateUserServiceImpl` is idempotent (no-op if missing).
- **Updates** are partial: only `displayName` is mutable through `UpdateUserCommand`; null fields are ignored.

### 4.2 Organizations

- **Creation** (`CreateOrganizationServiceImpl`):
  - Creates the `Organization`.
  - Automatically creates an `OrganizationMember` row for the creating user with role `ORGANIZATION_ADMIN`.
  - Publishes `OrganizationCreatedEvent`.
- **Membership** (`ManageOrganizationMemberServiceImpl`):
  - *Add* is idempotent: skips if the `(organizationId, userId)` pair already exists.
  - *Role update* is a no-op if the membership does not exist.
  - *Remove* deletes the membership row unconditionally.
- **Update** is partial (name/description only; null fields ignored) and throws `ResourceNotFoundException` if the org is missing.
- **Delete** removes the organization by id; **no cascading** of members, workspaces, or projects is performed at the service layer (defer to DB constraints/migrations for referential integrity).

### 4.3 Workspaces

- **Creation** (`CreateWorkspaceServiceImpl`):
  - Creates the `Workspace`.
  - Automatically creates a `WorkspaceMember` for `ownerId` with role `OWNER`.
  - Publishes `WorkspaceCreatedEvent`.
- **Slug uniqueness** is enforced at the database level (`unique` constraint); no explicit pre-check at the service layer.
- **Membership**: add is idempotent (same pattern as organizations); role update no-ops on missing; remove deletes unconditionally. Publishes `MemberAddedEvent` on add.
- **Update** partial (name/description). **Delete** has no cascade.

### 4.4 Projects

- **Creation** (`CreateProjectServiceImpl`): sets `status = ACTIVE` (hardcoded default), publishes `ProjectCreatedEvent`.
- **Status change** (`ChangeProjectStatusServiceImpl`): captures old status, sets new status, publishes `ProjectStatusChangedEvent`. Allowed values: `ACTIVE`, `ON_HOLD`, `COMPLETED`, `ARCHIVED`.
- **Update** partial (name/description only; status is changed via the dedicated status service).
- **Delete** has no cascade to tasks/members.

### 4.5 Tasks

- **Creation** (`CreateTaskServiceImpl`):
  - Defaults `status = TODO`.
  - Defaults `priority = MEDIUM` and `taskType = TASK` when not provided.
  - Publishes `TaskCreatedEvent`.
- **Subtasks**: `parentTaskId` links a task to a parent; `ListSubtasksQuery` enumerates children.
- **Status change** (`ChangeTaskStatusServiceImpl`): publishes `TaskStatusChangedEvent`. Allowed values: `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`, `BLOCKED`.
- **Assignment** (`AssignTaskServiceImpl`): assign is idempotent (no duplicate `TaskAssignee`); unassign deletes unconditionally. Publishes `TaskAssignedEvent` on assign.
- **Tags** (`ManageTaskTagServiceImpl`): add is idempotent (no duplicate tag name per task); remove deletes by `(taskId, tagName)`.
- **Update** partial (title/description/priority only).
- **Delete** has no cascade to assignees, tags, or comments.

### 4.6 Comments

- **Creation** requires `taskId`, `authorId`, `content`. Max content length is `CommentConstants.MAX_COMMENT_LENGTH` (10000); enforcement is expected via request validation (`@Size`), not re-checked in the service.
- **Update** overwrites `content`; throws `ResourceNotFoundException` if missing.
- **Delete** is by id.
- **Ownership rules** are expressed only through permissions (`COMMENT_UPDATE_OWN`, `COMMENT_DELETE_OWN` vs `COMMENT_DELETE_ANY`); there is no per-row ownership check in the service layer.

### 4.7 Notifications

- **Creation** defaults `notificationType = INFO` and `status = UNREAD`. References a target entity via optional `entityType` + `entityId`.
- **Status lifecycle**: `UNREAD → READ → ARCHIVED` (`NotificationStatus`).
- **Mark read** (single) is idempotent; `markAllAsReadByUserId` bulk-updates via `@Modifying` query.
- **Types**: `INFO`, `ASSIGNMENT`, `STATUS_CHANGE`, `ALERT`.

## 5. Domain Events

Services publish inter-module events via `ApplicationEventPublisher`. These are the system's extension points for side effects (e.g., notifying users). Currently there are **no `@EventListener` consumers**; events are emitted for future decoupled reactions.

| Event | Publisher | Payload intent |
|-------|-----------|----------------|
| `UserCreatedEvent` | `CreateUserServiceImpl` | New user provisioned. |
| `UserDeactivatedEvent` | (defined in `iam/events`) | User deactivated. |
| `OrganizationCreatedEvent` | `CreateOrganizationServiceImpl` | Org created. |
| `OrganizationMemberAddedEvent` | `ManageOrganizationMemberServiceImpl` | Member added to org. |
| `WorkspaceCreatedEvent` | `CreateWorkspaceServiceImpl` | Workspace created. |
| `MemberAddedEvent` | `ManageWorkspaceMemberServiceImpl` | Member added to workspace. |
| `ProjectCreatedEvent` | `CreateProjectServiceImpl` | Project created. |
| `ProjectStatusChangedEvent` | `ChangeProjectStatusServiceImpl` | Project status transitioned. |
| `TaskCreatedEvent` | `CreateTaskServiceImpl` | Task created. |
| `TaskStatusChangedEvent` | `ChangeTaskStatusServiceImpl` | Task status transitioned. |
| `TaskAssignedEvent` | `AssignTaskServiceImpl` | User assigned to task. |

## 6. Cross-Cutting Rules

### 6.1 Idempotency

Membership/assignment/tag "add" operations are idempotent by checking for existing `(parentId, userId)` or `(taskId, tagName)` pairs before inserting. Remove/delete operations are unconditional and idempotent by nature.

### 6.2 Partial Updates

All update services apply only non-null command fields, leaving the rest untouched. This means update commands never wipe existing data by omission.

### 6.3 Status Transition Freedom

Status changes (`Task`, `Project`) are **unconstrained** — any status may transition to any other. There is no state machine or allowed-transition matrix; `ChangeTaskStatusServiceImpl` and `ChangeProjectStatusServiceImpl` simply overwrite the status string.

### 6.4 CQRS

- **Commands** (`commands/`) are immutable records dispatched to `@Transactional` services that mutate state and publish events.
- **Queries** (`queries/`) are immutable records dispatched to `@Transactional(readOnly = true)` services returning projections/entities.
- List endpoints use cursor-based pagination (`CursorPage`) with `limit` constrained to `1..100` and default `20`.

### 6.5 Auditing

Every base table has a `*_h` shadow table maintained by `fn_audit_history()`; `created_by`/`updated_by` are populated from `SET LOCAL app.current_user` injected by `AuditContextInterceptor`.

## 7. Key Invariants & Constraints

- `identityId`, `username`, `email` are unique on `User`.
- `identifier` is unique on `Organization`; `slug` is unique on `Workspace`.
- Every `Organization` creation yields exactly one admin membership for its creator.
- Every `Workspace` creation yields exactly one `OWNER` membership for its owner.
- A task defaults to `TODO`/`MEDIUM`/`TASK`; a project defaults to `ACTIVE`; a notification defaults to `UNREAD`/`INFO`.
- Membership roles default to `ORGANIZATION_MEMBER` (org) and `MEMBER` (workspace/project).
