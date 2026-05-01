--liquibase formatted sql

--changeset mohammadreza:1769800000-add-actor-id-to-notifications
ALTER TABLE notifications ADD COLUMN actor_id UUID REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_notifications_actor_id ON notifications(actor_id);
