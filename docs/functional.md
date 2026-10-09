# Functional Specification

This document describes what Deep Project **does** from a user/feature perspective: the capabilities available, the API endpoints that expose them, and who can use them. For the *rules* behind these capabilities (invariants, defaults, RBAC matrix), see `business-logic.md`.

## 1. Overview

Deep Project is a multi-tenant project and task management application. It lets organizations organize work into **workspaces**, **projects**, and **tasks**, collaborate via **comments**, and stay informed through **notifications**. Access is protected by Keycloak-based single sign-on; authorization is enforced per-endpoint with fine-grained permissions.

The primary interface is a REST API (`/api/**`), documented interactively via Swagger UI.

## 2. Authentication & Identity

| Capability | Endpoint | Access |
|------------|----------|--------|
| Placeholder login endpoint | `POST /api/auth/login` | Public |
| Retrieve my profile (roles, permissions, organization memberships) | `GET /api/users/me` | Any authenticated user |
| List all users | `GET /api/users` | `SYSTEM_ADMIN` |
| Get a user | `GET /api/users/{id}` | `SYSTEM_ADMIN` or self |
| Create a user | `POST /api/users` | `SYSTEM_ADMIN` |
| Update a user (display name) | `PUT /api/users/{id}` | `SYSTEM_ADMIN` or self |
| Deactivate a user (soft) | `DELETE /api/users/{id}` | `SYSTEM_ADMIN` |

**User provisioning**: the first time an authenticated Keycloak identity calls `GET /api/users/me`, the system auto-provisions a local `User` record linked to the identity.

## 3. Organizations

Organizations are the top-level tenants.

| Capability | Endpoint | Access |
|------------|----------|--------|
| Get an organization | `GET /api/organizations/{id}` | `PERMISSION_ORG_READ` |
| Create an organization | `POST /api/organizations?userId=` | `PERMISSION_ORG_CREATE` |
| Update an organization (name/description) | `PUT /api/organizations/{id}` | `PERMISSION_ORG_UPDATE` |
| Delete an organization | `DELETE /api/organizations/{id}` | `PERMISSION_ORG_DELETE` |
| List members (paginated) | `GET /api/organizations/{id}/members` | `PERMISSION_ORG_READ` |
| Add a member | `POST /api/organizations/{id}/members?userId=&role=` | `PERMISSION_ORG_MEMBER_MANAGE` |
| Remove a member | `DELETE /api/organizations/{id}/members/{userId}` | `PERMISSION_ORG_MEMBER_MANAGE` |
| Change a member's role | `PUT /api/organizations/{id}/members/{userId}/role?role=` | `PERMISSION_ORG_MEMBER_MANAGE` |

Creating an organization automatically adds the creator as an `ORGANIZATION_ADMIN`.

## 4. Workspaces

Workspaces group projects and belong (optionally) to an organization.

| Capability | Endpoint | Access |
|------------|----------|--------|
| List a user's workspaces (paginated) | `GET /api/workspaces?userId=` | `PERMISSION_WORKSPACE_READ` |
| Get a workspace | `GET /api/workspaces/{id}` | `PERMISSION_WORKSPACE_READ` |
| Create a workspace | `POST /api/workspaces?ownerId=` | `PERMISSION_WORKSPACE_CREATE` |
| Update a workspace (name/description) | `PUT /api/workspaces/{id}` | `PERMISSION_WORKSPACE_UPDATE` |
| Delete a workspace | `DELETE /api/workspaces/{id}` | `PERMISSION_WORKSPACE_DELETE` |
| List members | `GET /api/workspaces/{id}/members` | `PERMISSION_WORKSPACE_READ` |
| Add a member | `POST /api/workspaces/{id}/members?userId=&role=` | `PERMISSION_WORKSPACE_MEMBER_MANAGE` |
| Remove a member | `DELETE /api/workspaces/{id}/members/{userId}` | `PERMISSION_WORKSPACE_MEMBER_MANAGE` |
| Change a member's role | `PUT /api/workspaces/{id}/members/{userId}/role?role=` | `PERMISSION_WORKSPACE_MEMBER_MANAGE` |

Creating a workspace automatically adds the owner as `OWNER`. Each workspace has a unique `slug`.

## 5. Projects

Projects live inside workspaces and track a lifecycle status.

| Capability | Endpoint | Access |
|------------|----------|--------|
| List projects in a workspace (paginated) | `GET /api/workspaces/{workspaceId}/projects` | `PERMISSION_PROJECT_READ` |
| Get a project | `GET /api/workspaces/{workspaceId}/projects/{id}` | `PERMISSION_PROJECT_READ` |
| Create a project | `POST /api/workspaces/{workspaceId}/projects` | `PERMISSION_PROJECT_CREATE` |
| Update a project (name/description) | `PUT /api/workspaces/{workspaceId}/projects/{id}` | `PERMISSION_PROJECT_UPDATE` |
| Change project status | `PATCH /api/workspaces/{workspaceId}/projects/{id}/status?status=` | `PERMISSION_PROJECT_UPDATE` |
| Delete a project | `DELETE /api/workspaces/{workspaceId}/projects/{id}` | `PERMISSION_PROJECT_DELETE` |
| List members | `GET /api/workspaces/{workspaceId}/projects/{id}/members` | `PERMISSION_PROJECT_READ` |
| Add a member | `POST /api/workspaces/{workspaceId}/projects/{id}/members?userId=&role=` | `PERMISSION_PROJECT_MEMBER_MANAGE` |
| Remove a member | `DELETE /api/workspaces/{workspaceId}/projects/{id}/members/{userId}` | `PERMISSION_PROJECT_MEMBER_MANAGE` |
| Change a member's role | `PUT /api/workspaces/{workspaceId}/projects/{id}/members/{userId}/role?role=` | `PERMISSION_PROJECT_MEMBER_MANAGE` |

Project statuses: `ACTIVE`, `ON_HOLD`, `COMPLETED`, `ARCHIVED`.

## 6. Tasks

Tasks belong to projects and support subtasks, assignments, tags, and a status workflow.

| Capability | Endpoint | Access |
|------------|----------|--------|
| List tasks in a project (paginated) | `GET /api/projects/{projectId}/tasks` | `PERMISSION_TASK_READ` |
| Get a task | `GET /api/projects/{projectId}/tasks/{id}` | `PERMISSION_TASK_READ` |
| List subtasks | `GET /api/projects/{projectId}/tasks/{id}/subtasks` | `PERMISSION_TASK_READ` |
| Create a task | `POST /api/projects/{projectId}/tasks` | `PERMISSION_TASK_CREATE` |
| Update a task (title/description/priority) | `PUT /api/projects/{projectId}/tasks/{id}` | `PERMISSION_TASK_UPDATE` |
| Change task status | `PATCH /api/projects/{projectId}/tasks/{id}/status?status=` | `PERMISSION_TASK_STATUS_CHANGE` |
| Assign a user | `POST /api/projects/{projectId}/tasks/{id}/assign?userId=` | `PERMISSION_TASK_ASSIGN` |
| Unassign a user | `DELETE /api/projects/{projectId}/tasks/{id}/assign/{userId}` | `PERMISSION_TASK_ASSIGN` |
| Add a tag | `POST /api/projects/{projectId}/tasks/{id}/tags?tagName=` | `PERMISSION_TASK_UPDATE` |
| Remove a tag | `DELETE /api/projects/{projectId}/tasks/{id}/tags/{tagName}` | `PERMISSION_TASK_UPDATE` |
| List assignees | `GET /api/projects/{projectId}/tasks/{id}/assignees` | `PERMISSION_TASK_READ` |
| List tags | `GET /api/projects/{projectId}/tasks/{id}/tags` | `PERMISSION_TASK_READ` |
| Delete a task | `DELETE /api/projects/{projectId}/tasks/{id}` | `PERMISSION_TASK_DELETE` |

- Task statuses: `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`, `BLOCKED`.
- Priorities: `LOW`, `MEDIUM`, `HIGH`, `URGENT`.
- Types: `TASK`, `BUG`, `FEATURE`, `EPIC`.

## 7. Comments

Comments are threaded on tasks.

| Capability | Endpoint | Access |
|------------|----------|--------|
| List comments on a task (paginated) | `GET /api/tasks/{taskId}/comments` | `PERMISSION_COMMENT_READ` |
| Get a comment | `GET /api/tasks/{taskId}/comments/{id}` | `PERMISSION_COMMENT_READ` |
| Add a comment | `POST /api/tasks/{taskId}/comments?authorId=` | `PERMISSION_COMMENT_CREATE` |
| Update a comment | `PUT /api/tasks/{taskId}/comments/{id}` | `PERMISSION_COMMENT_UPDATE_OWN` |
| Delete a comment | `DELETE /api/tasks/{taskId}/comments/{id}` | `PERMISSION_COMMENT_DELETE_OWN` or `PERMISSION_COMMENT_DELETE_ANY` |

## 8. Notifications

Notifications inform users of events and support read-state management.

| Capability | Endpoint | Access |
|------------|----------|--------|
| List a user's notifications (paginated) | `GET /api/notifications?userId=` | `PERMISSION_NOTIFICATION_READ` |
| List a user's unread notifications | `GET /api/notifications/unread?userId=` | `PERMISSION_NOTIFICATION_READ` |
| Mark one notification read | `PATCH /api/notifications/{id}/read` | `PERMISSION_NOTIFICATION_MANAGE` |
| Mark all a user's notifications read | `PATCH /api/notifications/read-all?userId=` | `PERMISSION_NOTIFICATION_MANAGE` |

Notification statuses: `UNREAD`, `READ`, `ARCHIVED`. Types: `INFO`, `ASSIGNMENT`, `STATUS_CHANGE`, `ALERT`.

## 9. Cross-Cutting Functionality

- **Pagination**: all list endpoints return a cursor-based page (`CursorPage`); `limit` is bounded to `1..100` (default `20`).
- **Error handling**: uniform `ProblemDetail` responses via `GlobalExceptionHandler`; missing resources return `ResourceNotFoundException`.
- **Auditing**: every table maintains an automatic `*_h` history shadow table; `created_by`/`updated_by` capture the acting user.
- **Observability**: optional OpenTelemetry (Grafana LGTM) stack for traces/metrics/logs.
- **API documentation**: Swagger UI with Keycloak OAuth2 authorization-code + PKCE flow for interactive testing.
