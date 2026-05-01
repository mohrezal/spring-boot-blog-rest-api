--liquibase formatted sql
--changeset mohammadreza:create_refresh_tokens_table context:schema splitStatements:false

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    device_name VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_refresh_tokens_expires_at CHECK (expires_at > created_at)
);

CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_user_id_revoked_at ON refresh_tokens(user_id, revoked_at);

--rollback DROP INDEX IF EXISTS idx_refresh_tokens_user_id_revoked_at;
--rollback DROP INDEX IF EXISTS idx_refresh_tokens_expires_at;
--rollback DROP INDEX IF EXISTS idx_refresh_tokens_user_id;
--rollback DROP INDEX IF EXISTS idx_refresh_tokens_token_hash;
--rollback DROP TABLE IF EXISTS refresh_tokens CASCADE;
