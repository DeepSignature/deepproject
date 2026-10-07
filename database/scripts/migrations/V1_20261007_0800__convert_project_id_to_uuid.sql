-- V3: Convert projects.id from BIGSERIAL/BIGINT to UUID
-- Data loss is acceptable; no backfill of FK references

-- 1. Drop audit history triggers and tables
DROP TRIGGER IF EXISTS trg_projects_audit ON projects;
DROP TRIGGER IF EXISTS trg_tasks_audit ON tasks;
DROP TRIGGER IF EXISTS trg_project_members_audit ON project_members;
DROP TABLE IF EXISTS projects_h;
DROP TABLE IF EXISTS tasks_h;
DROP TABLE IF EXISTS project_members_h;

-- 2. Drop foreign key constraints explicitly
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS tasks_project_id_fkey;
ALTER TABLE project_members DROP CONSTRAINT IF EXISTS project_members_project_id_fkey;

-- 3. Convert projects.id from BIGINT to UUID
ALTER TABLE projects ADD COLUMN new_id UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE projects DROP COLUMN id CASCADE;
ALTER TABLE projects RENAME COLUMN new_id TO id;
ALTER TABLE projects ADD PRIMARY KEY (id);

-- 4. Convert tasks.project_id from BIGINT to UUID
ALTER TABLE tasks ADD COLUMN new_project_id UUID;
ALTER TABLE tasks DROP COLUMN project_id;
ALTER TABLE tasks RENAME COLUMN new_project_id TO project_id;
ALTER TABLE tasks ALTER COLUMN project_id SET NOT NULL;

-- 5. Convert project_members.project_id from BIGINT to UUID
ALTER TABLE project_members ADD COLUMN new_project_id UUID;
ALTER TABLE project_members DROP COLUMN project_id;
ALTER TABLE project_members RENAME COLUMN new_project_id TO project_id;
ALTER TABLE project_members ALTER COLUMN project_id SET NOT NULL;

-- 6. Re-add foreign key constraints
ALTER TABLE tasks ADD CONSTRAINT tasks_project_id_fkey
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;
ALTER TABLE project_members ADD CONSTRAINT project_members_project_id_fkey
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

-- 7. Rebuild indexes
CREATE INDEX IF NOT EXISTS idx_projects_workspace ON projects (workspace_id);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects (status);
CREATE INDEX IF NOT EXISTS idx_tasks_project ON tasks (project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_parent ON tasks (parent_task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks (status);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks (priority);
CREATE INDEX IF NOT EXISTS idx_pm_project ON project_members (project_id);
CREATE INDEX IF NOT EXISTS idx_pm_user ON project_members (user_id);

-- 8. Re-enable audit history for affected tables
SELECT fn_enable_audit('projects');
SELECT fn_enable_audit('tasks');
SELECT fn_enable_audit('project_members');