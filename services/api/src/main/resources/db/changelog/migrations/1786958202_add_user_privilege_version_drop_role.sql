--liquibase formatted sql
--changeset mohammadreza:add_user_privilege_version_drop_role context:schema splitStatements:false
ALTER TABLE users
    ADD COLUMN privilege_version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_role_check;

ALTER TABLE users
    DROP COLUMN role;

--rollback ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';
--rollback ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'USER'));
--rollback ALTER TABLE users DROP COLUMN privilege_version;
