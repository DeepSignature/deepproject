-- ============================================================
-- Local Test & Development Seed Data
-- Only executed in local development environment
-- ============================================================

-- 1. Seed Organizations
INSERT INTO organizations (id, identifier, name, description)
VALUES
    (1, 'deep-engineering', 'Deep Engineering', 'Core engineering organization'),
    (2, 'product-mgmt', 'Product Management', 'Product roadmap and design org')
ON CONFLICT (id) DO NOTHING;
SELECT setval('organizations_id_seq', (SELECT COALESCE(MAX(id), 1) FROM organizations));

-- 2. Seed IAM Users
INSERT INTO users (id, identity_id, username, email, display_name, active)
VALUES
    (1, 'kc-admin-00001', 'admin', 'admin@deepproject.local', 'Admin User', true),
    (2, 'kc-john-00002', 'john_doe', 'john@deepproject.local', 'John Doe', true),
    (3, 'kc-jane-00003', 'jane_smith', 'jane@deepproject.local', 'Jane Smith', true)
ON CONFLICT (id) DO NOTHING;
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));

-- 3. Seed IAM Roles
INSERT INTO roles (id, name, description)
VALUES
    (1, 'ADMIN', 'System Administrator with full access'),
    (2, 'MEMBER', 'Standard Member'),
    (3, 'VIEWER', 'Read-only Viewer')
ON CONFLICT (id) DO NOTHING;
SELECT setval('roles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM roles));

-- 4. Seed User Roles
INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 2)
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 5. Seed Organization Members
INSERT INTO organization_members (id, organization_id, user_id, role)
VALUES
    (1, 1, 1, 'ORGANIZATION_ADMIN'),
    (2, 1, 2, 'ORGANIZATION_MEMBER'),
    (3, 1, 3, 'ORGANIZATION_MEMBER'),
    (4, 2, 2, 'ORGANIZATION_ADMIN'),
    (5, 2, 3, 'ORGANIZATION_MEMBER')
ON CONFLICT (organization_id, user_id) DO NOTHING;
SELECT setval('organization_members_id_seq', (SELECT COALESCE(MAX(id), 1) FROM organization_members));

-- 6. Seed Workspaces
INSERT INTO workspaces (id, name, slug, description, owner_id, organization_id)
VALUES
    (1, 'Deep Engineering', 'deep-engineering', 'Main workspace for core engineering projects', 1, 1),
    (2, 'Product Management', 'product-mgmt', 'Workspace for product roadmap and design', 2, 2)
ON CONFLICT (id) DO NOTHING;
SELECT setval('workspaces_id_seq', (SELECT COALESCE(MAX(id), 1) FROM workspaces));

-- 7. Seed Workspace Members
INSERT INTO workspace_members (id, workspace_id, user_id, role)
VALUES
    (1, 1, 1, 'OWNER'),
    (2, 1, 2, 'ADMIN'),
    (3, 1, 3, 'MEMBER'),
    (4, 2, 2, 'OWNER'),
    (5, 2, 3, 'MEMBER')
ON CONFLICT (workspace_id, user_id) DO NOTHING;
SELECT setval('workspace_members_id_seq', (SELECT COALESCE(MAX(id), 1) FROM workspace_members));

-- 8. Seed Projects
INSERT INTO projects (id, workspace_id, name, description, status)
VALUES
    (1, 1, 'Project Alpha', 'Core backend platform development', 'ACTIVE'),
    (2, 1, 'Project Beta', 'Mobile app client integration', 'ON_HOLD'),
    (3, 2, 'Q4 Roadmap', 'Product roadmap planning and user research', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;
SELECT setval('projects_id_seq', (SELECT COALESCE(MAX(id), 1) FROM projects));

-- 9. Seed Project Members
INSERT INTO project_members (id, project_id, user_id, role)
VALUES
    (1, 1, 1, 'OWNER'),
    (2, 1, 2, 'MEMBER'),
    (3, 1, 3, 'MEMBER'),
    (4, 3, 2, 'OWNER')
ON CONFLICT (project_id, user_id) DO NOTHING;
SELECT setval('project_members_id_seq', (SELECT COALESCE(MAX(id), 1) FROM project_members));

-- 10. Seed Tasks
INSERT INTO tasks (id, project_id, parent_task_id, title, description, status, priority, task_type)
VALUES
    (1, 1, NULL, 'Set up database migrations & auditing', 'Configure Flyway and shadow tables for audit trail', 'DONE', 'HIGH', 'TASK'),
    (2, 1, NULL, 'Implement Task Management CQRS API', 'Create command and query services for task manipulation', 'IN_PROGRESS', 'HIGH', 'FEATURE'),
    (3, 1, 2, 'Write unit tests for Task services', 'Add unit and architecture boundary tests', 'TODO', 'MEDIUM', 'TASK'),
    (4, 3, NULL, 'User feedback interviews analysis', 'Consolidate user responses from September cohort', 'TODO', 'LOW', 'TASK')
ON CONFLICT (id) DO NOTHING;
SELECT setval('tasks_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tasks));

-- 11. Seed Task Assignees & Tags
INSERT INTO task_assignees (id, task_id, user_id)
VALUES
    (1, 1, 1),
    (2, 2, 2),
    (3, 3, 2),
    (4, 4, 3)
ON CONFLICT (task_id, user_id) DO NOTHING;
SELECT setval('task_assignees_id_seq', (SELECT COALESCE(MAX(id), 1) FROM task_assignees));

INSERT INTO task_tags (id, task_id, tag_name)
VALUES
    (1, 1, 'database'),
    (2, 1, 'infrastructure'),
    (3, 2, 'backend'),
    (4, 2, 'cqrs'),
    (5, 3, 'testing')
ON CONFLICT (task_id, tag_name) DO NOTHING;
SELECT setval('task_tags_id_seq', (SELECT COALESCE(MAX(id), 1) FROM task_tags));

-- 12. Seed Comments
INSERT INTO comments (id, task_id, author_id, content)
VALUES
    (1, 1, 1, 'Flyway V1 script with automated audit trigger loop completed successfully.'),
    (2, 2, 2, 'Working on the AssignTaskService implementation now.')
ON CONFLICT (id) DO NOTHING;
SELECT setval('comments_id_seq', (SELECT COALESCE(MAX(id), 1) FROM comments));

-- 13. Seed Notifications
INSERT INTO notifications (id, user_id, title, message, notification_type, status, entity_type, entity_id)
VALUES
    (1, 2, 'Task Assigned', 'You have been assigned to task #2: Implement Task Management CQRS API', 'ASSIGNMENT', 'UNREAD', 'TASK', 2),
    (2, 3, 'Added to Workspace', 'You have been added to Deep Engineering as MEMBER', 'INFO', 'READ', 'WORKSPACE', 1)
ON CONFLICT (id) DO NOTHING;
SELECT setval('notifications_id_seq', (SELECT COALESCE(MAX(id), 1) FROM notifications));