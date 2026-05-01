--liquibase formatted sql
--changeset mohammadreza:drop_type_from_notifications context:schema

DROP INDEX IF EXISTS idx_type;
ALTER TABLE notifications DROP COLUMN IF EXISTS type;

--rollback ALTER TABLE notifications ADD COLUMN type VARCHAR(100);
--rollback CREATE INDEX idx_type ON notifications(type);