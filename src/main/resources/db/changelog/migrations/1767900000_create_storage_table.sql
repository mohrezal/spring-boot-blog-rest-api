--liquibase formatted sql
--changeset mohammadreza:create_storage_table context:schema splitStatements:false

CREATE TABLE storage (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    filename VARCHAR(255) UNIQUE NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    title VARCHAR(255),
    alt VARCHAR(255),

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_storage_user_id ON storage(user_id);
CREATE INDEX idx_storage_filename ON storage(filename);
CREATE INDEX idx_storage_created_at ON storage(created_at DESC);

--rollback DROP INDEX IF EXISTS idx_storage_created_at;
--rollback DROP INDEX IF EXISTS idx_storage_filename;
--rollback DROP INDEX IF EXISTS idx_storage_user_id;
--rollback DROP TABLE IF EXISTS storage CASCADE;
