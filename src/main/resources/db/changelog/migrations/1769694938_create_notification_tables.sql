--liquibase formatted sql
--changeset mohammadreza:create_notification_tables  context:schema splitStatements:false

CREATE TABLE notifications(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    data JSONB,
    is_read BOOLEAN NOT NULL,
    read_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recipient_id on notifications(recipient_id);
CREATE INDEX idx_type on notifications(type);
CREATE INDEX idx_is_read on notifications(is_read);
CREATE INDEX idx_read_at on notifications(read_at);

CREATE TABLE notification_preferences(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    in_app_enabled BOOLEAN NOT NULL,
    email_enabled BOOLEAN NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_user_id on notification_preferences(user_id);

--rollback DROP TABLE IF EXISTS notification_preferences;
--rollback DROP TABLE IF EXISTS notifications;
