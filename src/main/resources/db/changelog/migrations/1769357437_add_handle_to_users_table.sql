--liquibase formatted sql
--changeset mohammadreza:add_handle_to_users_table context:schema splitStatements:false

ALTER TABLE users ADD COLUMN handle VARCHAR(30) NOT NULL UNIQUE;

--rollback ALTER TABLE users DROP COLUMN IF EXISTS handle;
