-- V6: Add tasks.completed_at for the task dashboard read model
-- Recreate the tasks audit history table so its column set matches the new column
-- (the fn_audit_history() trigger copies NEW.* positionally into tasks_h).

ALTER TABLE tasks ADD COLUMN completed_at TIMESTAMPTZ;

DROP TRIGGER IF EXISTS trg_tasks_audit ON tasks;
DROP TABLE IF EXISTS tasks_h;

SELECT fn_enable_audit('tasks');

CREATE INDEX IF NOT EXISTS idx_tasks_completed_at ON tasks (completed_at);
