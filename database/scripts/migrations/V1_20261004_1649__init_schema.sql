-- ============================================================
-- Keycloak schema (must exist before Keycloak starts)
-- ============================================================
CREATE SCHEMA IF NOT EXISTS keycloak;

-- ============================================================
-- Audit trigger function: copies old/new row to <table>_h
-- ============================================================
CREATE OR REPLACE FUNCTION fn_audit_history()
RETURNS TRIGGER AS $$
DECLARE
    current_usr TEXT;
    history_table_name TEXT;
BEGIN
    current_usr := current_setting('app.current_user', true);
    IF current_usr IS NULL OR current_usr = '' THEN
        current_usr := 'system';
    END IF;

    history_table_name := TG_TABLE_NAME || '_h';

    IF (TG_OP = 'DELETE') THEN
        EXECUTE format(
            'INSERT INTO %I SELECT nextval(''%I_audit_id_seq''), ($1).*, ''DELETE'', CURRENT_TIMESTAMP, $2',
            history_table_name, history_table_name)
        USING OLD, current_usr;
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        EXECUTE format(
            'INSERT INTO %I SELECT nextval(''%I_audit_id_seq''), ($1).*, ''UPDATE'', CURRENT_TIMESTAMP, $2',
            history_table_name, history_table_name)
        USING NEW, current_usr;
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        EXECUTE format(
            'INSERT INTO %I SELECT nextval(''%I_audit_id_seq''), ($1).*, ''INSERT'', CURRENT_TIMESTAMP, $2',
            history_table_name, history_table_name)
        USING NEW, current_usr;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Helper: enable audit history for a single table
-- ============================================================
CREATE OR REPLACE FUNCTION fn_enable_audit(target_table TEXT)
RETURNS VOID AS $$
DECLARE
    h_table  TEXT := target_table || '_h';
    trg_name TEXT := 'trg_' || target_table || '_audit';
    idx_id   TEXT := 'idx_' || h_table || '_id';
    idx_ts   TEXT := 'idx_' || h_table || '_ts';
BEGIN
    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I (
            audit_id BIGSERIAL PRIMARY KEY,
            LIKE %I INCLUDING DEFAULTS,
            audit_operation VARCHAR(10) NOT NULL,
            audit_timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            audit_user VARCHAR(100)
        )', h_table, target_table);

    EXECUTE format('CREATE INDEX IF NOT EXISTS %I ON %I (id)', idx_id, h_table);
    EXECUTE format('CREATE INDEX IF NOT EXISTS %I ON %I (audit_timestamp)', idx_ts, h_table);

    EXECUTE format('DROP TRIGGER IF EXISTS %I ON %I', trg_name, target_table);
    EXECUTE format('CREATE TRIGGER %I AFTER INSERT OR UPDATE OR DELETE ON %I FOR EACH ROW EXECUTE FUNCTION fn_audit_history()',
                   trg_name, target_table);
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- IAM
-- ============================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    identity_id VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE (user_id, role_id)
);

-- ============================================================
-- WORKSPACES
-- ============================================================
CREATE TABLE workspaces (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    organization_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_workspaces_owner ON workspaces (owner_id);

CREATE TABLE workspace_members (
    id BIGSERIAL PRIMARY KEY,
    workspace_id BIGINT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE (workspace_id, user_id)
);
CREATE INDEX idx_ws_members_workspace ON workspace_members (workspace_id);
CREATE INDEX idx_ws_members_user ON workspace_members (user_id);

-- ============================================================
-- PROJECTS
-- ============================================================
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    workspace_id BIGINT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    start_date TIMESTAMPTZ,
    end_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_projects_workspace ON projects (workspace_id);
CREATE INDEX idx_projects_status ON projects (status);

CREATE TABLE project_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE (project_id, user_id)
);
CREATE INDEX idx_pm_project ON project_members (project_id);
CREATE INDEX idx_pm_user ON project_members (user_id);

-- ============================================================
-- TASKS
-- ============================================================
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    parent_task_id BIGINT REFERENCES tasks(id),
    title VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    task_type VARCHAR(50) NOT NULL DEFAULT 'TASK',
    due_date TIMESTAMPTZ,
    estimated_hours NUMERIC(10, 2),
    actual_hours NUMERIC(10, 2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_tasks_project ON tasks (project_id);
CREATE INDEX idx_tasks_parent ON tasks (parent_task_id);
CREATE INDEX idx_tasks_status ON tasks (status);
CREATE INDEX idx_tasks_priority ON tasks (priority);

CREATE TABLE task_assignees (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE (task_id, user_id)
);
CREATE INDEX idx_ta_task ON task_assignees (task_id);
CREATE INDEX idx_ta_user ON task_assignees (user_id);

CREATE TABLE task_tags (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    tag_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE (task_id, tag_name)
);
CREATE INDEX idx_tt_task ON task_tags (task_id);

-- ============================================================
-- COMMENTS
-- ============================================================
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_comments_task ON comments (task_id);
CREATE INDEX idx_comments_author ON comments (author_id);

-- ============================================================
-- NOTIFICATIONS
-- ============================================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(300) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL DEFAULT 'INFO',
    status VARCHAR(50) NOT NULL DEFAULT 'UNREAD',
    entity_type VARCHAR(50),
    entity_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_notifications_user ON notifications (user_id);
CREATE INDEX idx_notifications_status ON notifications (status);
CREATE INDEX idx_notifications_type ON notifications (notification_type);

-- ============================================================
-- AUTO-ENABLE AUDIT on all domain tables
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