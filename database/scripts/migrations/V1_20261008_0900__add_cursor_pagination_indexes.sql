-- V5: Add composite indexes supporting keyset (cursor) pagination on (created_at, id)
-- Each index mirrors the ORDER BY used by the list endpoints.

CREATE INDEX IF NOT EXISTS idx_users_created_id ON users (created_at, id);

CREATE INDEX IF NOT EXISTS idx_workspaces_created_id ON workspaces (created_at, id);

CREATE INDEX IF NOT EXISTS idx_projects_workspace_created_id ON projects (workspace_id, created_at, id);

CREATE INDEX IF NOT EXISTS idx_tasks_project_created_id ON tasks (project_id, created_at, id);
CREATE INDEX IF NOT EXISTS idx_tasks_parent_created_id ON tasks (parent_task_id, created_at, id);

CREATE INDEX IF NOT EXISTS idx_comments_task_created_id ON comments (task_id, created_at, id);

CREATE INDEX IF NOT EXISTS idx_notifications_user_created_id ON notifications (user_id, created_at DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_user_status_created_id ON notifications (user_id, status, created_at DESC, id DESC);

CREATE INDEX IF NOT EXISTS idx_org_members_org_created_id ON organization_members (organization_id, created_at, id);
