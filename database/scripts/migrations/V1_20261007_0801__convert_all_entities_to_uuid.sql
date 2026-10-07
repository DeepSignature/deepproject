-- V4: Convert all remaining entity PKs and FK columns from BIGINT to UUID
-- Projects table and its FK references already converted in earlier migration
-- Data loss is acceptable

-- ============================================================
-- 1. Drop all audit triggers and history tables
-- ============================================================
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN (SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_name LIKE '%_h') LOOP
        EXECUTE format('DROP TABLE IF EXISTS %I CASCADE', r.table_name);
    END LOOP;
END;
$$;

-- ============================================================
-- 2. Drop all FK constraints
-- ============================================================
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_user_id_fkey;
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_role_id_fkey;
ALTER TABLE workspaces DROP CONSTRAINT IF EXISTS workspaces_owner_id_fkey;
ALTER TABLE workspaces DROP CONSTRAINT IF EXISTS fk_workspaces_org;
ALTER TABLE workspace_members DROP CONSTRAINT IF EXISTS workspace_members_workspace_id_fkey;
ALTER TABLE workspace_members DROP CONSTRAINT IF EXISTS workspace_members_user_id_fkey;
ALTER TABLE projects DROP CONSTRAINT IF EXISTS projects_workspace_id_fkey;
ALTER TABLE project_members DROP CONSTRAINT IF EXISTS project_members_user_id_fkey;
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS tasks_parent_task_id_fkey;
ALTER TABLE task_assignees DROP CONSTRAINT IF EXISTS task_assignees_task_id_fkey;
ALTER TABLE task_assignees DROP CONSTRAINT IF EXISTS task_assignees_user_id_fkey;
ALTER TABLE task_tags DROP CONSTRAINT IF EXISTS task_tags_task_id_fkey;
ALTER TABLE comments DROP CONSTRAINT IF EXISTS comments_task_id_fkey;
ALTER TABLE comments DROP CONSTRAINT IF EXISTS comments_author_id_fkey;
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_user_id_fkey;
ALTER TABLE organization_members DROP CONSTRAINT IF EXISTS organization_members_organization_id_fkey;
ALTER TABLE organization_members DROP CONSTRAINT IF EXISTS organization_members_user_id_fkey;

-- ============================================================
-- 3. Convert PKs for all tables (skip projects — already UUID)
-- ============================================================

-- users
ALTER TABLE users ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE users DROP COLUMN id CASCADE;
ALTER TABLE users RENAME COLUMN id_new TO id;
ALTER TABLE users ADD PRIMARY KEY (id);

-- roles
ALTER TABLE roles ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE roles DROP COLUMN id CASCADE;
ALTER TABLE roles RENAME COLUMN id_new TO id;
ALTER TABLE roles ADD PRIMARY KEY (id);

-- organizations
ALTER TABLE organizations ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE organizations DROP COLUMN id CASCADE;
ALTER TABLE organizations RENAME COLUMN id_new TO id;
ALTER TABLE organizations ADD PRIMARY KEY (id);

-- workspaces
ALTER TABLE workspaces ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE workspaces DROP COLUMN id CASCADE;
ALTER TABLE workspaces RENAME COLUMN id_new TO id;
ALTER TABLE workspaces ADD PRIMARY KEY (id);

-- notifications
ALTER TABLE notifications ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE notifications DROP COLUMN id CASCADE;
ALTER TABLE notifications RENAME COLUMN id_new TO id;
ALTER TABLE notifications ADD PRIMARY KEY (id);

-- user_roles
ALTER TABLE user_roles ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE user_roles DROP COLUMN id CASCADE;
ALTER TABLE user_roles RENAME COLUMN id_new TO id;
ALTER TABLE user_roles ADD PRIMARY KEY (id);

-- organization_members
ALTER TABLE organization_members ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE organization_members DROP COLUMN id CASCADE;
ALTER TABLE organization_members RENAME COLUMN id_new TO id;
ALTER TABLE organization_members ADD PRIMARY KEY (id);

-- workspace_members
ALTER TABLE workspace_members ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE workspace_members DROP COLUMN id CASCADE;
ALTER TABLE workspace_members RENAME COLUMN id_new TO id;
ALTER TABLE workspace_members ADD PRIMARY KEY (id);

-- project_members
ALTER TABLE project_members ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE project_members DROP COLUMN id CASCADE;
ALTER TABLE project_members RENAME COLUMN id_new TO id;
ALTER TABLE project_members ADD PRIMARY KEY (id);

-- tasks
ALTER TABLE tasks ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE tasks DROP COLUMN id CASCADE;
ALTER TABLE tasks RENAME COLUMN id_new TO id;
ALTER TABLE tasks ADD PRIMARY KEY (id);

-- task_assignees
ALTER TABLE task_assignees ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE task_assignees DROP COLUMN id CASCADE;
ALTER TABLE task_assignees RENAME COLUMN id_new TO id;
ALTER TABLE task_assignees ADD PRIMARY KEY (id);

-- task_tags
ALTER TABLE task_tags ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE task_tags DROP COLUMN id CASCADE;
ALTER TABLE task_tags RENAME COLUMN id_new TO id;
ALTER TABLE task_tags ADD PRIMARY KEY (id);

-- comments
ALTER TABLE comments ADD COLUMN id_new UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE comments DROP COLUMN id CASCADE;
ALTER TABLE comments RENAME COLUMN id_new TO id;
ALTER TABLE comments ADD PRIMARY KEY (id);

-- ============================================================
-- 4. Convert FK columns (skip project_id FKs — already UUID)
-- ============================================================

-- user_roles
ALTER TABLE user_roles ADD COLUMN user_id_new UUID, ADD COLUMN role_id_new UUID;
ALTER TABLE user_roles DROP COLUMN user_id, DROP COLUMN role_id;
ALTER TABLE user_roles RENAME COLUMN user_id_new TO user_id;
ALTER TABLE user_roles RENAME COLUMN role_id_new TO role_id;
ALTER TABLE user_roles ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE user_roles ALTER COLUMN role_id SET NOT NULL;

-- workspaces
ALTER TABLE workspaces ADD COLUMN owner_id_new UUID, ADD COLUMN organization_id_new UUID;
ALTER TABLE workspaces DROP COLUMN owner_id, DROP COLUMN organization_id;
ALTER TABLE workspaces RENAME COLUMN owner_id_new TO owner_id;
ALTER TABLE workspaces RENAME COLUMN organization_id_new TO organization_id;
ALTER TABLE workspaces ALTER COLUMN owner_id SET NOT NULL;

-- projects.workspace_id (FK parent workspace converted above)
ALTER TABLE projects ADD COLUMN workspace_id_new UUID;
ALTER TABLE projects DROP COLUMN workspace_id;
ALTER TABLE projects RENAME COLUMN workspace_id_new TO workspace_id;
ALTER TABLE projects ALTER COLUMN workspace_id SET NOT NULL;

-- workspace_members
ALTER TABLE workspace_members ADD COLUMN workspace_id_new UUID, ADD COLUMN user_id_new UUID;
ALTER TABLE workspace_members DROP COLUMN workspace_id, DROP COLUMN user_id;
ALTER TABLE workspace_members RENAME COLUMN workspace_id_new TO workspace_id;
ALTER TABLE workspace_members RENAME COLUMN user_id_new TO user_id;
ALTER TABLE workspace_members ALTER COLUMN workspace_id SET NOT NULL;
ALTER TABLE workspace_members ALTER COLUMN user_id SET NOT NULL;

-- project_members.user_id (project_id already UUID)
ALTER TABLE project_members ADD COLUMN user_id_new UUID;
ALTER TABLE project_members DROP COLUMN user_id;
ALTER TABLE project_members RENAME COLUMN user_id_new TO user_id;
ALTER TABLE project_members ALTER COLUMN user_id SET NOT NULL;

-- tasks.parent_task_id
ALTER TABLE tasks ADD COLUMN parent_task_id_new UUID;
ALTER TABLE tasks DROP COLUMN parent_task_id;
ALTER TABLE tasks RENAME COLUMN parent_task_id_new TO parent_task_id;

-- task_assignees
ALTER TABLE task_assignees ADD COLUMN task_id_new UUID, ADD COLUMN user_id_new UUID;
ALTER TABLE task_assignees DROP COLUMN task_id, DROP COLUMN user_id;
ALTER TABLE task_assignees RENAME COLUMN task_id_new TO task_id;
ALTER TABLE task_assignees RENAME COLUMN user_id_new TO user_id;
ALTER TABLE task_assignees ALTER COLUMN task_id SET NOT NULL;
ALTER TABLE task_assignees ALTER COLUMN user_id SET NOT NULL;

-- task_tags
ALTER TABLE task_tags ADD COLUMN task_id_new UUID;
ALTER TABLE task_tags DROP COLUMN task_id;
ALTER TABLE task_tags RENAME COLUMN task_id_new TO task_id;
ALTER TABLE task_tags ALTER COLUMN task_id SET NOT NULL;

-- comments
ALTER TABLE comments ADD COLUMN task_id_new UUID, ADD COLUMN author_id_new UUID;
ALTER TABLE comments DROP COLUMN task_id, DROP COLUMN author_id;
ALTER TABLE comments RENAME COLUMN task_id_new TO task_id;
ALTER TABLE comments RENAME COLUMN author_id_new TO author_id;
ALTER TABLE comments ALTER COLUMN task_id SET NOT NULL;
ALTER TABLE comments ALTER COLUMN author_id SET NOT NULL;

-- notifications
ALTER TABLE notifications ADD COLUMN user_id_new UUID, ADD COLUMN entity_id_new UUID;
ALTER TABLE notifications DROP COLUMN user_id, DROP COLUMN entity_id;
ALTER TABLE notifications RENAME COLUMN user_id_new TO user_id;
ALTER TABLE notifications RENAME COLUMN entity_id_new TO entity_id;
ALTER TABLE notifications ALTER COLUMN user_id SET NOT NULL;

-- organization_members
ALTER TABLE organization_members ADD COLUMN organization_id_new UUID, ADD COLUMN user_id_new UUID;
ALTER TABLE organization_members DROP COLUMN organization_id, DROP COLUMN user_id;
ALTER TABLE organization_members RENAME COLUMN organization_id_new TO organization_id;
ALTER TABLE organization_members RENAME COLUMN user_id_new TO user_id;
ALTER TABLE organization_members ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE organization_members ALTER COLUMN user_id SET NOT NULL;

-- ============================================================
-- 5. Re-add all FK constraints
-- ============================================================
ALTER TABLE user_roles ADD CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE user_roles ADD CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;
ALTER TABLE workspaces ADD CONSTRAINT workspaces_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES users(id);
ALTER TABLE workspaces ADD CONSTRAINT fk_workspaces_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE;
ALTER TABLE workspace_members ADD CONSTRAINT workspace_members_workspace_id_fkey FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE;
ALTER TABLE workspace_members ADD CONSTRAINT workspace_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE projects ADD CONSTRAINT projects_workspace_id_fkey FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE;
ALTER TABLE project_members ADD CONSTRAINT project_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE tasks ADD CONSTRAINT tasks_parent_task_id_fkey FOREIGN KEY (parent_task_id) REFERENCES tasks(id);
ALTER TABLE task_assignees ADD CONSTRAINT task_assignees_task_id_fkey FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;
ALTER TABLE task_assignees ADD CONSTRAINT task_assignees_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE task_tags ADD CONSTRAINT task_tags_task_id_fkey FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;
ALTER TABLE comments ADD CONSTRAINT comments_task_id_fkey FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;
ALTER TABLE comments ADD CONSTRAINT comments_author_id_fkey FOREIGN KEY (author_id) REFERENCES users(id);
ALTER TABLE notifications ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE organization_members ADD CONSTRAINT organization_members_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE;
ALTER TABLE organization_members ADD CONSTRAINT organization_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- ============================================================
-- 6. Rebuild all indexes
-- ============================================================
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_identity_id ON users (identity_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_roles_unique ON user_roles (user_id, role_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_roles_name ON roles (name);
CREATE UNIQUE INDEX IF NOT EXISTS idx_workspaces_slug ON workspaces (slug);
CREATE INDEX IF NOT EXISTS idx_workspaces_owner ON workspaces (owner_id);
CREATE INDEX IF NOT EXISTS idx_workspaces_org ON workspaces (organization_id);
CREATE INDEX IF NOT EXISTS idx_ws_members_workspace ON workspace_members (workspace_id);
CREATE INDEX IF NOT EXISTS idx_ws_members_user ON workspace_members (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_ws_members_unique ON workspace_members (workspace_id, user_id);
CREATE INDEX IF NOT EXISTS idx_projects_workspace ON projects (workspace_id);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects (status);
CREATE INDEX IF NOT EXISTS idx_pm_project ON project_members (project_id);
CREATE INDEX IF NOT EXISTS idx_pm_user ON project_members (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_pm_unique ON project_members (project_id, user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_project ON tasks (project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_parent ON tasks (parent_task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks (status);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks (priority);
CREATE UNIQUE INDEX IF NOT EXISTS idx_ta_unique ON task_assignees (task_id, user_id);
CREATE INDEX IF NOT EXISTS idx_ta_task ON task_assignees (task_id);
CREATE INDEX IF NOT EXISTS idx_ta_user ON task_assignees (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_tt_unique ON task_tags (task_id, tag_name);
CREATE INDEX IF NOT EXISTS idx_tt_task ON task_tags (task_id);
CREATE INDEX IF NOT EXISTS idx_comments_task ON comments (task_id);
CREATE INDEX IF NOT EXISTS idx_comments_author ON comments (author_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications (user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications (status);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications (notification_type);
CREATE UNIQUE INDEX IF NOT EXISTS idx_organizations_identifier ON organizations (identifier);
CREATE INDEX IF NOT EXISTS idx_org_members_org ON organization_members (organization_id);
CREATE INDEX IF NOT EXISTS idx_org_members_user ON organization_members (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_org_members_unique ON organization_members (organization_id, user_id);

-- ============================================================
-- 7. Re-enable audit for all domain tables
-- ============================================================
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN (
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_type = 'BASE TABLE'
          AND table_name NOT LIKE '%\_h' ESCAPE '\'
          AND table_name NOT IN ('flyway_schema_history')
    ) LOOP
        PERFORM fn_enable_audit(r.table_name);
    END LOOP;
END;
$$;