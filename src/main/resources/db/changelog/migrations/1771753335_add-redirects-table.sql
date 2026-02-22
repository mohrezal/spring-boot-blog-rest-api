--liquibase formatted sql
--changeset mohammadreza:add-redirects-table context:schema splitStatements:false
CREATE TABLE redirects(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    code VARCHAR(50) NOT NULL UNIQUE,
    target_type VARCHAR(50) NOT NULL,
    target_id UUID NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_redirects_code UNIQUE (code),
    CONSTRAINT uq_redirects_code_target_type UNIQUE (code, target_type)

);

CREATE INDEX idx_redirects_target ON redirects(code, target_type);

--rollback DROP TABLE redirects;
